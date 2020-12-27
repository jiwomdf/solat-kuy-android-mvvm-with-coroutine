package com.programmergabut.solatkuy.ui.activitymain

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseActivity
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.databinding.ActivityMainBinding
import com.programmergabut.solatkuy.databinding.LayoutBottomsheetBygpsBinding
import com.programmergabut.solatkuy.databinding.LayoutBottomsheetBylatitudelongitudeBinding
import com.programmergabut.solatkuy.databinding.LayoutFristopenappBinding
import com.programmergabut.solatkuy.ui.SolatKuyFragmentFactory
import com.programmergabut.solatkuy.util.EnumStatus
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import org.joda.time.LocalDate
import javax.inject.Inject

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>(
    R.layout.activity_main, MainActivityViewModel::class.java
) {

    private val ALL_PERMISSIONS = 101
    private lateinit var bottomSheetDialog: Dialog
    private lateinit var firstOpenDialog: Dialog
    private lateinit var bsByGpsBinding: LayoutBottomsheetBygpsBinding
    private lateinit var dialogBinding: LayoutFristopenappBinding

    @Inject
    lateinit var fragmentFactory: SolatKuyFragmentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflateBinding()
        supportFragmentManager.fragmentFactory = fragmentFactory
    }

    override fun setListener() {
        observeDb()
        observeErrorMsg()
    }

    override fun onDestroy() {
        setIsHasOpenAnimation(false)
        super.onDestroy()
    }

    private fun inflateBinding() {
        dialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.layout_fristopenapp, null, true
        )
    }

    private fun observeDb(){
        viewModel.msSetting.observe(this, {
            if(it != null){
                if(it.isHasOpenApp)
                    initBottomNav()
                else
                    initDialog()
            }
            else{
                SolatKuyRoom.populateDatabase(getDatabase())
            }
        })
    }

    private fun observeErrorMsg() {
        viewModel.errMsApi1Status.observe(this, {
            val errMsg = viewModel.getErrMsApi1Msg()
            if(errMsg.isEmpty())
                return@observe

            when(it) {
                EnumStatus.SUCCESS -> {
                    Toasty.success(this, errMsg, Toasty.LENGTH_SHORT).show()
                    updateIsHasOpenApp()
                }
                EnumStatus.ERROR -> {
                    Toasty.error(this, errMsg, Toasty.LENGTH_SHORT).show()
                }
                else -> {/* NO-OP */}
            }
        })
    }

    private fun initDialog() {
        firstOpenDialog =  Dialog(this@MainActivity)
        firstOpenDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        firstOpenDialog.setCancelable(false)
        firstOpenDialog.setContentView(dialogBinding.root)
        firstOpenDialog.show()

        btnSetLatitudeLongitude()
    }

    private fun initBottomNav() {
        try{
            binding.bottomNavigation.setupWithNavController(binding.navHostFragment.findNavController())
            binding.navHostFragment.findNavController()
                .addOnDestinationChangedListener { _, destination, _ ->
                    when(destination.id){
                        R.id.fragmentMain, R.id.fragmentCompass, R.id.quranFragment, R.id.fragmentSetting ->
                            binding.bottomNavigation.visibility = View.VISIBLE
                        else -> binding.bottomNavigation.visibility = View.GONE
                    }
                }
            binding.bottomNavigation.setOnNavigationItemReselectedListener {/* NO-OP */ }
        }
        catch (ex: Exception){
            print(ex.message)
        }
    }

    /* Function listener */
    private fun btnSetLatitudeLongitude(){
        bottomSheetDialog = BottomSheetDialog(this)

        dialogBinding.btnByLatitudeLongitude.setOnClickListener {
            val bsByLatLngBinding = DataBindingUtil.inflate<LayoutBottomsheetBylatitudelongitudeBinding>(
                LayoutInflater.from(this),
                R.layout.layout_bottomsheet_bylatitudelongitude, null, true
            )
            bottomSheetDialog.setContentView(bsByLatLngBinding.root)
            bottomSheetDialog.show()
            bsByLatLngBinding.btnProceedByLL.setOnClickListener byLl@{
                val latitude = bsByLatLngBinding.etLlDialogLatitude.text.toString().trim()
                val longitude = bsByLatLngBinding.etLlDialogLongitude.text.toString().trim()
                insertLocationSettingToDb(latitude, longitude)
            }
        }

        dialogBinding.btnByGps.setOnClickListener{
            bsByGpsBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.layout_bottomsheet_bygps, null, true
            )
            bottomSheetDialog.setContentView(bsByGpsBinding.root)
            bottomSheetDialog.show()
            getGPSLocation()
            bsByGpsBinding.btnProceedByGps.setOnClickListener byGps@{
                val latitude = bsByGpsBinding.tvGpsDialogLatitude.text.toString().trim()
                val longitude = bsByGpsBinding.tvGpsDialogLongitude.text.toString().trim()
                insertLocationSettingToDb(latitude, longitude)
            }
        }
    }

    private fun updateIsHasOpenApp() {
        bottomSheetDialog.dismiss()
        firstOpenDialog.dismiss()
        viewModel.updateIsHasOpenApp(true)
    }

    /* Database Transaction */
    private fun insertLocationSettingToDb(latitude: String, longitude: String) {
        val currDate = LocalDate()
        val data = MsApi1(1,
            latitude,
            longitude,
            "3",
            currDate.monthOfYear.toString(),
            currDate.year.toString())

        viewModel.updateMsApi1(data)
    }

    /* Permission */
    private fun showPermissionDialog(){
        AlertDialog.Builder(this)
            .setTitle("Location Needed")
            .setMessage("Permission is needed to run the Gps")
            .setPositiveButton("ok") { _: DialogInterface, _: Int ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    ALL_PERMISSIONS
                )
            }
            .setNegativeButton("cancel") { _: DialogInterface, _: Int ->
                bottomSheetDialog.dismiss()
            }
            .create()
            .show()
    }

    /* supporting function */
    private fun getGPSLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setGpsBottomSheetState()
            onUpdateLocationListener()
        }
        else {
            showPermissionDialog()
            return
        }
    }

    private fun setGpsBottomSheetState() {
        val lm: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: java.lang.Exception) { }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: java.lang.Exception) { }

        if (!gpsEnabled && !networkEnabled)
            setGpsComponentState(EnumStatus.ERROR)
        else
            setGpsComponentState(EnumStatus.LOADING)
    }

    private fun onUpdateLocationListener(){
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 10 * 10000 /* 1 minute */
        mLocationRequest.fastestInterval = 10 * 10000 /* 1 minute */

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showPermissionDialog()
            return
        }

        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(mLocationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    // do work here
                    onLocationChanged(locationResult.lastLocation)
                }
                private fun onLocationChanged(it: Location?) {
                    if(it == null)
                        return
                    setGpsComponentState(EnumStatus.SUCCESS)
                    bsByGpsBinding.tvGpsDialogLatitude.text = it.latitude.toString()
                    bsByGpsBinding.tvGpsDialogLongitude.text= it.longitude.toString()
                }
            }, Looper.myLooper())
    }

    private fun setGpsComponentState(status: EnumStatus){
        when(status){
            EnumStatus.SUCCESS -> {
                bsByGpsBinding.tvViewLatitude.visibility = View.VISIBLE
                bsByGpsBinding.tvViewLongitude.visibility = View.VISIBLE
                bsByGpsBinding.tvGpsDialogLongitude.visibility = View.VISIBLE
                bsByGpsBinding.tvGpsDialogLatitude.visibility = View.VISIBLE
                bsByGpsBinding.ivWarning.visibility = View.INVISIBLE
                bsByGpsBinding.tvWarning.visibility = View.INVISIBLE
            }
            EnumStatus.LOADING -> {
                bsByGpsBinding.ivWarning.visibility = View.VISIBLE
                bsByGpsBinding.tvWarning.visibility = View.VISIBLE
                bsByGpsBinding.tvWarning.text = getString(R.string.loading)
                bsByGpsBinding.tvViewLatitude.visibility = View.INVISIBLE
                bsByGpsBinding.tvViewLongitude.visibility = View.INVISIBLE
                bsByGpsBinding.tvGpsDialogLongitude.visibility = View.INVISIBLE
                bsByGpsBinding.tvGpsDialogLatitude.visibility = View.INVISIBLE
            }
            EnumStatus.ERROR -> {
                bsByGpsBinding.ivWarning.visibility = View.VISIBLE
                bsByGpsBinding.tvWarning.visibility = View.VISIBLE
                bsByGpsBinding.tvWarning.text = getString(R.string.please_enable_your_location)
                bsByGpsBinding.tvGpsDialogLongitude.visibility = View.INVISIBLE
                bsByGpsBinding.tvGpsDialogLatitude.visibility = View.INVISIBLE
                bsByGpsBinding.tvViewLatitude.visibility = View.INVISIBLE
                bsByGpsBinding.tvViewLongitude.visibility = View.INVISIBLE
            }
        }
    }

    /* VIEW PAGER */
    /* override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.i_prayer_time -> vp2_main.currentItem = 0
            R.id.i_compass -> vp2_main.currentItem = 1
            R.id.i_quran -> vp2_main.currentItem = 2
            R.id.i_info -> vp2_main.currentItem = 3
            R.id.i_setting -> vp2_main.currentItem = 4
        }
        return true
    } */

    /* Animation */
    /* private inner class ZoomOutPageTransformer : ViewPager.PageTransformer, ViewPager2.PageTransformer {

        private val MIN_SCALE = 0.85f
        private val MIN_ALPHA = 0.5f
        override fun transformPage(view: View, position: Float) {
            view.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> alpha = 0f
                    position <= 1 -> {
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = MIN_SCALE.coerceAtLeast(1 - kotlin.math.abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) horzMargin - vertMargin / 2 else horzMargin + vertMargin / 2
                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor
                        // Fade the page relative to its size.
                        alpha = (MIN_ALPHA + (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> alpha = 0f
                }
            }
        }
    } */


}
