package com.programmergabut.solatkuy.ui.main

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_bottomsheet_bygps.view.*
import kotlinx.android.synthetic.main.layout_bottomsheet_bylatitudelongitude.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import javax.inject.Inject

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var mSubDialogView: View
    private lateinit var mSubDialog: Dialog
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    @Inject lateinit var db : SolatKuyRoom
    @Inject lateinit var sharedPref: SharedPreferences
    private val ALL_PERMISSIONS = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkFirstOpenApp()
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPref.edit().clear().apply()
    }

    /* First load activity */
    private fun checkFirstOpenApp() {

        mainActivityViewModel.msSetting.observe(this, Observer {
            when(it.status){
                EnumStatus.SUCCESS -> {
                    if(it.data != null)
                        if(it.data.isHasOpenApp){
                            //initViewPager()
                            initBottomNav()
                        }
                        else
                            initDialog()
                    else
                        SolatKuyRoom.populateDatabase(db)
                }
                EnumStatus.LOADING -> {
                    print("loading")
                }
                EnumStatus.ERROR -> {
                    print("error")
                }
            }
        })

    }

    /* DIALOG */
    private fun initDialog() {
        val dialogView = layoutInflater.inflate(R.layout.layout_fristopenapp,null)
        val dialog =  Dialog(this@MainActivity)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()

        btnSetLatitudeLongitude(dialog)
    }

    private fun initViewPager() {
        /* val adapter = SwipeAdapter(supportFragmentManager, lifecycle)
        adapter.addFragment(FragmentMain(false))
        adapter.addFragment(FragmentCompass())
        adapter.addFragment(QuranFragment())
        adapter.addFragment(FragmentInfo())
        adapter.addFragment(FragmentSetting())

        vp2_main.let {
            it.adapter = adapter
            it.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            it.setPageTransformer(ZoomOutPageTransformer())
            it.isUserInputEnabled = false
            (it.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        } */

        /* addOnLayoutChangeListener( object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> bottom_navigation.menu.findItem(R.id.i_prayer_time).isChecked  = true
                    1 -> bottom_navigation.menu.findItem(R.id.i_compass).isChecked = true
                    2 -> bottom_navigation.menu.findItem(R.id.i_quran).isChecked = true
                    3 -> bottom_navigation.menu.findItem(R.id.i_info).isChecked = true
                    4 -> bottom_navigation.menu.findItem(R.id.i_setting).isChecked = true
                    else -> error("MainActivity")
                }
            }

        }) */
    }

    private fun initBottomNav() {

        try{
            bottom_navigation.setupWithNavController(navHostFragment.findNavController())

            navHostFragment.findNavController()
                .addOnDestinationChangedListener { _, destination, _ ->
                    when(destination.id){
                        R.id.fragmentMain, R.id.fragmentCompass, R.id.quranFragment, R.id.fragmentInfo, R.id.fragmentSetting ->
                            bottom_navigation.visibility = View.VISIBLE
                        else -> bottom_navigation.visibility = View.GONE
                    }
                }
        }catch (ex: Exception){
            print(ex.message)
        }

    }

    /* Function listener */
    private fun btnSetLatitudeLongitude(dialog: Dialog){

        val btnByLatitudeLongitude = dialog.findViewById<Button>(R.id.btn_byLatitudeLongitude)
        val btnByGps = dialog.findViewById<Button>(R.id.btn_byGps)

        mSubDialog = BottomSheetDialog(this)

        btnByLatitudeLongitude.setOnClickListener {

            mSubDialogView = layoutInflater.inflate(R.layout.layout_bottomsheet_bylatitudelongitude,null)
            mSubDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mSubDialog.setContentView(mSubDialogView)
            mSubDialog.show()

            mSubDialogView.btn_proceedByLL.setOnClickListener byLl@{

                val latitude = mSubDialogView.et_llDialog_latitude.text.toString().trim()
                val longitude = mSubDialogView.et_llDialog_longitude.text.toString().trim()

                if(latitude.isEmpty() || longitude.isEmpty() || latitude == "." || longitude == "."){
                    Toasty.warning(this@MainActivity,"latitude and longitude cannot be empty", Toast.LENGTH_SHORT).show()
                    return@byLl
                }

                val arrLatitude = latitude.toCharArray()
                val arrLongitude = longitude.toCharArray()

                if(arrLatitude[arrLatitude.size - 1] == '.' || arrLongitude[arrLongitude.size - 1] == '.'){
                    Toasty.warning(this@MainActivity,"latitude and longitude cannot be ended by .", Toast.LENGTH_SHORT).show()
                    return@byLl
                }

                if(arrLatitude[0] == '.' || arrLongitude[0] == '.'){
                    Toasty.warning(this@MainActivity,"latitude and longitude cannot be started by .", Toast.LENGTH_SHORT).show()
                    return@byLl
                }

                insertLocationSettingToDb(latitude, longitude)
                updateIsHasOpenApp(dialog)
            }
        }

        btnByGps.setOnClickListener{
            mSubDialogView = layoutInflater.inflate(R.layout.layout_bottomsheet_bygps,null)
            mSubDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mSubDialog.setContentView(mSubDialogView)
            mSubDialog.show()

            getGPSLocation(mSubDialogView)

            mSubDialogView.btn_proceedByGps.setOnClickListener byGps@{

                val latitude = mSubDialogView.tv_gpsDialog_latitude.text.toString().trim()
                val longitude = mSubDialogView.tv_gpsDialog_longitude.text.toString().trim()

                if(latitude.isEmpty() || longitude.isEmpty()){
                    Toasty.warning(this@MainActivity,"Action failed, please enable your location", Toast.LENGTH_SHORT).show()
                    return@byGps
                }

                insertLocationSettingToDb(latitude, longitude)
                updateIsHasOpenApp(dialog)
            }
        }

    }

    private fun updateIsHasOpenApp(dialog: Dialog) {
        mSubDialog.dismiss()
        dialog.dismiss()

        lifecycleScope.launch(Dispatchers.IO){
            db.msSettingDao().updateIsHasOpenApp(true)
        }

        /* mainActivityViewModel.updateSetting(
            MsSetting(
                1,
                true
            )
        )*/
        Toasty.success(this@MainActivity,"Success change the coordinate", Toast.LENGTH_SHORT).show()

        initViewPager()
    }

    /* Database Transaction */
    private fun insertLocationSettingToDb(latitude: String, longitude: String) {

        val currDate = LocalDate()

        lifecycleScope.launch(Dispatchers.IO){
            db.msApi1Dao().updateMsApi1(1,
                latitude,
                longitude,
                "3",
                currDate.monthOfYear.toString(),
                currDate.year.toString())
        }

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
                mSubDialog.dismiss()
            }
            .create()
            .show()
    }

    /* supporting function */
    private fun getGPSLocation(dialogView: View){

        dialogView.findViewById<TextView>(R.id.tv_view_latitude).visibility = View.GONE
        dialogView.findViewById<TextView>(R.id.tv_view_longitude).visibility = View.GONE

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            //onSuccessListener()
            onFailedListener()
            onUpdateLocationListener()
        }
        else {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            showPermissionDialog()
            return
        }
    }

    private fun onFailedListener() {
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
            mSubDialogView.tv_warning.text = getString(R.string.please_enable_your_location)
        else
            mSubDialogView.tv_warning.text = getString(R.string.loading)
    }

    private fun onUpdateLocationListener(){
        val mLocationRequest: LocationRequest?
        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 10 * 10000 /* 1 minute */
        mLocationRequest.fastestInterval = 10 * 10000 /* 1 minute */

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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

                    mSubDialogView.iv_warning.visibility = View.GONE
                    mSubDialogView.tv_warning.visibility = View.GONE
                    mSubDialogView.findViewById<TextView>(R.id.tv_view_latitude).visibility = View.VISIBLE
                    mSubDialogView.findViewById<TextView>(R.id.tv_view_longitude).visibility = View.VISIBLE

                    mSubDialogView.tv_gpsDialog_latitude.text = it.latitude.toString()
                    mSubDialogView.tv_gpsDialog_longitude.text= it.longitude.toString()

                }
            }, Looper.myLooper())

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