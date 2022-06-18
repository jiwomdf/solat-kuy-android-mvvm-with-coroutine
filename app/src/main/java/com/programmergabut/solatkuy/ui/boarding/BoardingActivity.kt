package com.programmergabut.solatkuy.ui.boarding

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseActivity
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.databinding.ActivityBoardingBinding
import com.programmergabut.solatkuy.databinding.LayoutBottomsheetBygpsBinding
import com.programmergabut.solatkuy.databinding.LayoutBottomsheetBylatitudelongitudeBinding
import com.programmergabut.solatkuy.ui.main.MainActivity
import com.programmergabut.solatkuy.util.Constant
import com.programmergabut.solatkuy.util.Status
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import org.joda.time.LocalDate

@AndroidEntryPoint
class BoardingActivity : BaseActivity<ActivityBoardingBinding, BoardingViewModel>(
    R.layout.activity_boarding,
    BoardingViewModel::class.java
), View.OnClickListener {

    private lateinit var bsGpsBinding: LayoutBottomsheetBygpsBinding
    private lateinit var bsLatLngBinding: LayoutBottomsheetBylatitudelongitudeBinding
    private lateinit var bottomSheetDialog: Dialog
    private var isHasOpenSettingButton = false

    override fun getViewBinding(): ActivityBoardingBinding = ActivityBoardingBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bsLatLngBinding = LayoutBottomsheetBylatitudelongitudeBinding.inflate(layoutInflater)
        bsGpsBinding = LayoutBottomsheetBygpsBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(this)
        setListener()
    }

    override fun onStart() {
        super.onStart()
        if(isHasOpenSettingButton){
            getGPSLocation()
            isHasOpenSettingButton = false
        }
    }

    private fun setListener() {
        binding.apply {
            btnByLatitudeLongitude.setOnClickListener(this@BoardingActivity)
            btnByGps.setOnClickListener(this@BoardingActivity)
            bsGpsBinding.btnProceedByGps.setOnClickListener(this@BoardingActivity)
            bsLatLngBinding.btnProceedByLL.setOnClickListener(this@BoardingActivity)

            viewModel.msSetting.observe(this@BoardingActivity) {
                if (it != null && it.isHasOpenApp) {
                    gotoIntent(MainActivity::class.java, null, true)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_by_latitude_longitude -> {
                bottomSheetDialog.setContentView(bsLatLngBinding.root)
                bottomSheetDialog.show()
            }
            R.id.btn_proceedByLL -> {
                val latitude = bsLatLngBinding.etLlDialogLatitude.text.toString().trim()
                val longitude = bsLatLngBinding.etLlDialogLongitude.text.toString().trim()
                insertLocationSettingToDb(latitude, longitude)
            }
            R.id.btn_by_gps -> {
                bottomSheetDialog.setContentView(bsGpsBinding.root)
                bottomSheetDialog.show()
                getGPSLocation()
            }
            R.id.btn_proceedByGps -> {
                if(bsGpsBinding.tvGpsDialogLatitude.visibility != View.VISIBLE &&
                    bsGpsBinding.tvViewLongitude.visibility != View.VISIBLE){
                    isHasOpenSettingButton = true
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                } else {
                    val latitude = bsGpsBinding.tvGpsDialogLatitude.text.toString().trim()
                    val longitude = bsGpsBinding.tvGpsDialogLongitude.text.toString().trim()
                    insertLocationSettingToDb(latitude, longitude)
                }
            }
        }
    }

    private fun updateIsHasOpenApp() {
        bottomSheetDialog.dismiss()
        viewModel.updateIsHasOpenApp(true)
    }

    private fun insertLocationSettingToDb(latitude: String, longitude: String) {
        val msApi1 = MsApi1(
            1,
            latitude,
            longitude,
            Constant.STARTED_METHOD,
            LocalDate().monthOfYear.toString(),
            LocalDate().year.toString()
        )

        if(msApi1.latitude.isEmpty() || msApi1.longitude.isEmpty() || msApi1.latitude == "." || msApi1.longitude == "."){
            Toasty.error(this, getString(R.string.latitude_and_longitude_cannot_be_empty), Toasty.LENGTH_SHORT).show()
            return
        }

        val arrLatitude = msApi1.latitude.toCharArray()
        val arrLongitude = msApi1.longitude.toCharArray()

        if(arrLatitude[arrLatitude.size - 1] == '.' || arrLongitude[arrLongitude.size - 1] == '.'){
            Toasty.error(this, getString(R.string.latitude_and_longitude_cannot_be_ended_with_dot), Toasty.LENGTH_SHORT).show()
            return
        }

        if(arrLatitude[0] == '.' || arrLongitude[0] == '.'){
            Toasty.error(this, getString(R.string.latitude_and_longitude_cannot_be_started_with_dot), Toasty.LENGTH_SHORT).show()
            return
        }

        viewModel.updateMsApi1(msApi1)
        Toasty.success(this, getString(R.string.success_change_the_coordinate), Toasty.LENGTH_SHORT).show()

        updateIsHasOpenApp()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == LOCATION_PERMISSIONS){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                onUpdateLocationListener()
            } else {
                bottomSheetDialog.dismiss()
                Toasty.error(this, getString(R.string.permission_is_needed_to_run_the_gps), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionDialog(){
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.location_needed))
            .setMessage(getString(R.string.permission_is_needed_to_run_the_gps))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.oke)) { _: DialogInterface, _: Int ->
                ActivityCompat.requestPermissions(this, listLocationPermission(), LOCATION_PERMISSIONS)
            }
            .setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->
                bottomSheetDialog.dismiss()
            }
            .create()
            .show()
    }

    private fun getGPSLocation(){
        if (isLocationPermissionGranted()) {
            setGpsBottomSheetState()
            onUpdateLocationListener()
        } else {
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
            setGpsComponentState(Status.Error)
        else
            setGpsComponentState(Status.Loading)
    }

    @SuppressLint("MissingPermission")
    private fun onUpdateLocationListener(){
        val mLocationRequest = LocationRequest().also {
            it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            it.interval = 10 * 1000 /* 10 minute */
            it.fastestInterval = 1 * 1000 /* 1 second */
        }

        if (!isLocationPermissionGranted()) {
            showPermissionDialog()
            return
        }

        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(mLocationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    bsGpsBinding.apply {
                        val location = locationResult.lastLocation ?: return
                        setGpsComponentState(Status.Success)
                        tvGpsDialogLatitude.text = location.latitude.toString()
                        tvGpsDialogLongitude.text= location.longitude.toString()
                    }
                }
            }, Looper.myLooper())
    }

    private fun setGpsComponentState(status: Status){
        bsGpsBinding.apply {
            when(status){
                Status.Success -> {
                    tvViewLatitude.visibility = View.VISIBLE
                    tvViewLongitude.visibility = View.VISIBLE
                    tvGpsDialogLongitude.visibility = View.VISIBLE
                    tvGpsDialogLatitude.visibility = View.VISIBLE
                    ivWarning.visibility = View.INVISIBLE
                    tvWarning.visibility = View.INVISIBLE
                    btnProceedByGps.text = getString(R.string.proceed)
                    btnProceedByGps.visibility = View.VISIBLE
                    btnProceedByGps.text = getString(R.string.proceed)
                }
                Status.Loading -> {
                    ivWarning.visibility = View.VISIBLE
                    tvWarning.visibility = View.VISIBLE
                    tvWarning.text = getString(R.string.loading)
                    tvViewLatitude.visibility = View.INVISIBLE
                    tvViewLongitude.visibility = View.INVISIBLE
                    tvGpsDialogLongitude.visibility = View.INVISIBLE
                    tvGpsDialogLatitude.visibility = View.INVISIBLE
                    btnProceedByGps.visibility = View.INVISIBLE
                }
                Status.Error -> {
                    ivWarning.visibility = View.VISIBLE
                    tvWarning.visibility = View.VISIBLE
                    tvWarning.text = getString(R.string.please_enable_your_location)
                    tvGpsDialogLongitude.visibility = View.INVISIBLE
                    tvGpsDialogLatitude.visibility = View.INVISIBLE
                    tvViewLatitude.visibility = View.INVISIBLE
                    tvViewLongitude.visibility = View.INVISIBLE
                    btnProceedByGps.text = getString(R.string.open_setting)
                    btnProceedByGps.visibility = View.VISIBLE
                    btnProceedByGps.text = getString(R.string.open_setting)
                }
            }
        }
    }
}