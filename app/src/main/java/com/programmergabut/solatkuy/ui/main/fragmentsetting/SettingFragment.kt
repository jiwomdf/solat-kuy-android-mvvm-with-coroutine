package com.programmergabut.solatkuy.ui.main.fragmentsetting

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
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseFragment
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.databinding.FragmentSettingBinding
import com.programmergabut.solatkuy.databinding.LayoutAboutAuthorBinding
import com.programmergabut.solatkuy.databinding.LayoutBottomsheetBygpsBinding
import com.programmergabut.solatkuy.databinding.LayoutBottomsheetBylatitudelongitudeBinding
import com.programmergabut.solatkuy.util.EnumConfig
import com.programmergabut.solatkuy.util.EnumStatus
import com.programmergabut.solatkuy.util.helper.LocationHelper
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import org.joda.time.LocalDate

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

@AndroidEntryPoint
class SettingFragment(viewModelTest: FragmentSettingViewModel? = null) : BaseFragment<FragmentSettingBinding, FragmentSettingViewModel>(
    R.layout.fragment_setting,
    FragmentSettingViewModel::class.java, viewModelTest
), View.OnClickListener {

    private lateinit var aboutAuthorDialog: Dialog
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var dialogGpsBinding: LayoutBottomsheetBygpsBinding
    private lateinit var dialogLatLngBinding: LayoutBottomsheetBylatitudelongitudeBinding
    private lateinit var dialogAuthorBinding: LayoutAboutAuthorBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aboutAuthorDialog = Dialog(requireContext())
        bottomSheetDialog = BottomSheetDialog(requireContext())
    }

    override fun setListener() {
        super.setListener()
        inflateBinding()
        binding.btnByLatitudeLongitude.setOnClickListener(this)
        binding.btnByGps.setOnClickListener(this)
        binding.btnSeeAuthor.setOnClickListener(this)
        dialogGpsBinding.btnProceedByGps.setOnClickListener(this)
        dialogLatLngBinding.btnProceedByLL.setOnClickListener(this)
        subscribeObserversDB()
        observeErrorMsg()
    }

    private fun inflateBinding() {
        dialogGpsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.layout_bottomsheet_bygps, null, false
        )
        dialogLatLngBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.layout_bottomsheet_bylatitudelongitude, null, false
        )
        dialogAuthorBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.layout_about_author, null, false
        )
    }

    private fun subscribeObserversDB() {
        viewModel.msApi1.observe(viewLifecycleOwner, { retVal ->
            if(retVal != null){
                val city = LocationHelper.getCity(requireContext(), retVal.latitude.toDouble(), retVal.longitude.toDouble())
                binding.tvViewLatitude.text = retVal.latitude + " °S"
                binding.tvViewLongitude.text = retVal.longitude + " °E"
                binding.tvViewCity.text = city ?: EnumConfig.CITY_NOT_FOUND_STR
            }
        })
    }

    private fun observeErrorMsg() {
        viewModel.updateMessage.observe(viewLifecycleOwner, {
            when(it.status) {
                EnumStatus.SUCCESS -> {
                    if(it.message.isNullOrEmpty()){
                        showBottomSheet()
                        return@observe
                    }
                    Toasty.success(requireContext(), it.message, Toasty.LENGTH_SHORT).show()
                    bottomSheetDialog.dismiss()
                }
                EnumStatus.ERROR -> {
                    Toasty.error(requireContext(), it.message ?: "", Toasty.LENGTH_SHORT).show()
                }
                else -> {/* NO-OP */}
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_by_latitude_longitude -> {
                bottomSheetDialog.setContentView(dialogLatLngBinding.root)
                bottomSheetDialog.show()
            }
            R.id.btn_proceedByLL -> {
                val latitude = dialogLatLngBinding.etLlDialogLatitude.text.toString().trim()
                val longitude = dialogLatLngBinding.etLlDialogLongitude.text.toString().trim()
                insertLocationSettingToDb(latitude, longitude)
            }
            R.id.btn_by_gps -> {
                bottomSheetDialog.setContentView(dialogGpsBinding.root)
                bottomSheetDialog.show()
                getGPSLocation()
            }
            R.id.btn_proceedByGps -> {
                if(dialogGpsBinding.tvGpsDialogLatitude.visibility != View.VISIBLE &&
                    dialogGpsBinding.tvViewLongitude.visibility != View.VISIBLE){
                    startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                else {
                    val latitude = dialogGpsBinding.tvGpsDialogLatitude.text.toString().trim()
                    val longitude = dialogGpsBinding.tvGpsDialogLongitude.text.toString().trim()
                    insertLocationSettingToDb(latitude, longitude)
                }
            }
            R.id.btn_seeAuthor -> {
                aboutAuthorDialog.setContentView(dialogAuthorBinding.root)
                aboutAuthorDialog.show()
            }
        }
    }

    /* Database Transaction */
    private fun insertLocationSettingToDb(latitude: String, longitude: String) {
        val currDate = LocalDate()
        val data = MsApi1(
            1,
            latitude,
            longitude,
            "3",
            currDate.monthOfYear.toString(),
            currDate.year.toString()
        )

        viewModel.updateMsApi1(data)
    }

    /* permission */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == LOCATION_PERMISSIONS){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                onUpdateLocationListener()
            }
            else{
                bottomSheetDialog.dismiss()
                Toasty.error(requireContext(), getString(R.string.permission_is_needed_to_run_the_gps), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionDialog(){
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.location_needed))
            .setMessage(getString(R.string.permission_is_needed_to_run_the_gps))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.oke)) { _: DialogInterface, _: Int ->
                ActivityCompat.requestPermissions(requireActivity(), listLocationPermission(), LOCATION_PERMISSIONS)
            }
            .setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->
                bottomSheetDialog.dismiss()
            }
            .create()
            .show()
    }


    /* supporting function */
    private fun getGPSLocation(){
        if (isLocationPermissionGranted()) {
            mFusedLocationClient = getFusedLocationProviderClient(requireContext())
            onFailedListener()
            onUpdateLocationListener()
        }
        else {
            showPermissionDialog()
            return
        }
    }

    private fun onFailedListener() {
        val lm: LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

    @SuppressLint("MissingPermission")
    private fun onUpdateLocationListener() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 60 * 1000 /* 1 minute */
        mLocationRequest.fastestInterval = 1 * 1000 /* 1 second */
        if (!isLocationPermissionGranted()) {
            showPermissionDialog()
            return
        }
        getFusedLocationProviderClient(requireContext()).requestLocationUpdates(mLocationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation ?: return
                setGpsComponentState(EnumStatus.SUCCESS)
                dialogGpsBinding.tvGpsDialogLatitude.text = location.latitude.toString()
                dialogGpsBinding.tvGpsDialogLongitude.text= location.longitude.toString()
            }
        }, Looper.myLooper())
    }

    private fun setGpsComponentState(status: EnumStatus){
        when(status){
            EnumStatus.SUCCESS -> {
                dialogGpsBinding.tvViewLatitude.visibility = View.VISIBLE
                dialogGpsBinding.tvViewLongitude.visibility = View.VISIBLE
                dialogGpsBinding.tvGpsDialogLongitude.visibility = View.VISIBLE
                dialogGpsBinding.tvGpsDialogLatitude.visibility = View.VISIBLE
                dialogGpsBinding.ivWarning.visibility = View.INVISIBLE
                dialogGpsBinding.tvWarning.visibility = View.INVISIBLE
                dialogGpsBinding.btnProceedByGps.visibility = View.VISIBLE
                dialogGpsBinding.btnProceedByGps.text = getString(R.string.proceed)
            }
            EnumStatus.LOADING -> {
                dialogGpsBinding.ivWarning.visibility = View.VISIBLE
                dialogGpsBinding.tvWarning.visibility = View.VISIBLE
                dialogGpsBinding.tvWarning.text = getString(R.string.loading)
                dialogGpsBinding.tvViewLatitude.visibility = View.INVISIBLE
                dialogGpsBinding.tvViewLongitude.visibility = View.INVISIBLE
                dialogGpsBinding.tvGpsDialogLongitude.visibility = View.INVISIBLE
                dialogGpsBinding.tvGpsDialogLatitude.visibility = View.INVISIBLE
                dialogGpsBinding.btnProceedByGps.visibility = View.INVISIBLE
            }
            EnumStatus.ERROR -> {
                dialogGpsBinding.ivWarning.visibility = View.VISIBLE
                dialogGpsBinding.tvWarning.visibility = View.VISIBLE
                dialogGpsBinding.tvWarning.text = getString(R.string.please_enable_your_location)
                dialogGpsBinding.tvGpsDialogLongitude.visibility = View.INVISIBLE
                dialogGpsBinding.tvGpsDialogLatitude.visibility = View.INVISIBLE
                dialogGpsBinding.tvViewLatitude.visibility = View.INVISIBLE
                dialogGpsBinding.tvViewLongitude.visibility = View.INVISIBLE
                dialogGpsBinding.btnProceedByGps.visibility = View.VISIBLE
                dialogGpsBinding.btnProceedByGps.text = getString(R.string.open_setting)
            }
        }
    }

}
