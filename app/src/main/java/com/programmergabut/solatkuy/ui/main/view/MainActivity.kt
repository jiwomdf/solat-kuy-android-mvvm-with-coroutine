package com.programmergabut.solatkuy.ui.main.view

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.data.model.entity.MsSetting
import com.programmergabut.solatkuy.ui.main.adapter.SwipeAdapter
import com.programmergabut.solatkuy.ui.main.viewmodel.MainActivityViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_bottom_navigation_bar.*
import kotlinx.android.synthetic.main.layout_bottomsheet_bygps.view.*
import kotlinx.android.synthetic.main.layout_bottomsheet_bylatitudelongitude.view.*
import org.joda.time.LocalDate
import kotlin.math.abs

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var mSubDialogView: View
    lateinit var mSubDialog: Dialog
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private val ALL_PERMISSIONS = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        bottom_navigation.setOnNavigationItemSelectedListener(this)

        checkFirstOpenApp()
    }

    /* First load activity */
    private fun checkFirstOpenApp() {

        mainActivityViewModel.msSetting.observe(this, Observer {

            it?.let {
                if(it.isHasOpenApp)
                    initViewPager()
                else
                    initDialog()
            }
        })

    }

    private fun initDialog() {
        val dialogView = layoutInflater.inflate(R.layout.layout_fristopenapp,null)
        val dialog =  Dialog(this@MainActivity)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()

        btnSetLatitudeLongitude(dialog)
    }

    private fun initViewPager() {
        vp2_main.adapter = SwipeAdapter(supportFragmentManager)
        vp2_main.setPageTransformer(true, ZoomOutPageTransformer())
        vp2_main.addOnPageChangeListener( object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> bottom_navigation.menu.findItem(R.id.i_prayer_time).isChecked  = true
                    1 -> bottom_navigation.menu.findItem(R.id.i_prayer_time2).isChecked = true
                    2 -> bottom_navigation.menu.findItem(R.id.i_prayer_time3).isChecked = true
                }
            }

        })
    }

    /* Function listener */
    private fun btnSetLatitudeLongitude(dialog: Dialog){

        val btnByLatitudeLongitude = dialog.findViewById<Button>(R.id.btn_byLatitudeLongitude)
        val btnByGps = dialog.findViewById<Button>(R.id.btn_byGps)

        mSubDialog = BottomSheetDialog(this)

        btnByLatitudeLongitude.setOnClickListener {

            mSubDialogView = layoutInflater.inflate(R.layout.layout_bottomsheet_bylatitudelongitude,null)
            mSubDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                successIsFirstOpen(dialog)
            }
        }

        btnByGps.setOnClickListener{
            mSubDialogView = layoutInflater.inflate(R.layout.layout_bottomsheet_bygps,null)
            mSubDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                successIsFirstOpen(dialog)
            }
        }

    }

    private fun successIsFirstOpen(dialog: Dialog) {
        mSubDialog.dismiss()
        dialog.dismiss()

        mainActivityViewModel.updateSetting(MsSetting(1,true))
        Toasty.success(this@MainActivity,"Success change the coordinate", Toast.LENGTH_SHORT).show()

        initViewPager()
    }

    /* Database Transaction */
    private fun insertLocationSettingToDb(latitude: String, longitude: String) {

        val currDate = LocalDate()

        val data = MsApi1(
            1,
            latitude,
            longitude,
            "8",
            currDate.monthOfYear.toString(),
            currDate.year.toString()
        )

        mainActivityViewModel.updateMsApi1(data)
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
            mSubDialogView.tv_warning.text = "Please enable your location"
        else
            mSubDialogView.tv_warning.text = getString(R.string.loading)
    }

    private fun onUpdateLocationListener(){
        val mLocationRequest: LocationRequest?
        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 10 * 10000 /* 1 minute */
        mLocationRequest.fastestInterval = 10 * 10000 /* 1 minute */

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

    /* View pager */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.i_prayer_time -> vp2_main.currentItem = 0
            R.id.i_prayer_time2 -> vp2_main.currentItem = 1
            R.id.i_prayer_time3 -> vp2_main.currentItem = 2
        }
        return true
    }

    /* Animation */
    private inner class ZoomOutPageTransformer : ViewPager.PageTransformer {

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
                        val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
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

    }


}
