package com.programmergabut.solatkuy.ui.main.setting

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
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
import com.programmergabut.solatkuy.data.local.localentity.MsCalculationMethods
import com.programmergabut.solatkuy.ui.LocationHelper
import com.programmergabut.solatkuy.util.Constant
import com.programmergabut.solatkuy.util.Status
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import org.joda.time.LocalDate

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

@AndroidEntryPoint
class SettingFragment(viewModelTest: SettingViewModel? = null):
BaseFragment<FragmentSettingBinding, SettingViewModel>(
    R.layout.fragment_setting,
    SettingViewModel::class.java, viewModelTest
), View.OnClickListener {

    private lateinit var aboutDialog: Dialog
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var dialogGpsBinding: LayoutBottomsheetBygpsBinding
    private lateinit var dialogLatLngBinding: LayoutBottomsheetBylatitudelongitudeBinding
    private lateinit var dialogAuthorBinding: LayoutAboutAuthorBinding

    private var listMethods: MutableList<MsCalculationMethods>? = null
    private var isHasOpenSettingButton = false
    private var bindSpinnerCounter = 0

    override fun getViewBinding() = FragmentSettingBinding.inflate(layoutInflater)

    override fun onStart() {
        super.onStart()
        if(isHasOpenSettingButton){
            getGPSLocation()
            isHasOpenSettingButton = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aboutDialog = Dialog(requireContext())
        bottomSheetDialog = BottomSheetDialog(requireContext())
        dialogGpsBinding = LayoutBottomsheetBygpsBinding.inflate(layoutInflater)
        dialogLatLngBinding = LayoutBottomsheetBylatitudelongitudeBinding.inflate(layoutInflater)
        dialogAuthorBinding = LayoutAboutAuthorBinding.inflate(layoutInflater)

        setupSpinner()
        setListener()
        viewModel.getMethods()
    }

    private fun setListener() {
        binding.apply {
            btnByLatitudeLongitude.setOnClickListener(this@SettingFragment)
            btnByGps.setOnClickListener(this@SettingFragment)
            btnSeeAuthor.setOnClickListener(this@SettingFragment)
        }

        dialogGpsBinding.btnProceedByGps.setOnClickListener(this)
        dialogLatLngBinding.btnProceedByLL.setOnClickListener(this)

        viewModel.methods.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS, Status.ERROR -> {
                    if (it?.data != null) {
                        binding.sMethods.adapter = ArrayAdapter(requireContext(),
                            android.R.layout.simple_list_item_1,
                            it.data.map { method -> method.name }
                        )
                        setMethode(it.data)
                    }
                    listMethods = it?.data?.toMutableList()
                }
                Status.LOADING -> {}
            }
        }

        viewModel.msApi1.observe(viewLifecycleOwner) { retVal ->
            binding.apply {
                if (retVal != null) {
                    val city = LocationHelper.getCity(
                        requireContext(),
                        retVal.latitude.toDouble(),
                        retVal.longitude.toDouble()
                    )
                    tvViewLatitude.text = retVal.latitude + " °S"
                    tvViewLongitude.text = retVal.longitude + " °E"
                    tvViewCity.text = city ?: Constant.CITY_NOT_FOUND
                }
            }
        }
    }

    private fun setMethode(datas: List<MsCalculationMethods>) {
        binding.apply {
            val methodId = sharedPrefUtil.getSelectedMethod()
            val method = datas.find { method -> method.method == methodId }
            if(methodId < 0 || method == null){
                sMethods.setSelection(12)
                tvViewMethod.text = datas[12].name
            } else {
                sMethods.setSelection(method.index)
                tvViewMethod.text = method.name
            }
        }
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
                    isHasOpenSettingButton = true
                    startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                } else {
                    val latitude = dialogGpsBinding.tvGpsDialogLatitude.text.toString().trim()
                    val longitude = dialogGpsBinding.tvGpsDialogLongitude.text.toString().trim()
                    insertLocationSettingToDb(latitude, longitude)
                    bottomSheetDialog.dismiss()
                }
            }
            R.id.btn_seeAuthor -> {
                aboutDialog.setContentView(dialogAuthorBinding.root)
                aboutDialog.show()
            }
        }
    }

    private fun setupSpinner(){
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            emptyArray()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        binding.apply {
            sMethods.adapter = adapter
            sMethods.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if(bindSpinnerCounter >= 1){
                        val method = listMethods?.find { method -> method.index == position }
                        if(method != null){
                            sharedPrefUtil.insertSelectedMethod(method.method)
                            viewModel.updateMsApi1Method(1 , method.method.toString())
                            tvViewMethod.text = method.name
                        }
                    }
                    bindSpinnerCounter++
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun insertLocationSettingToDb(latitude: String, longitude: String) {
        val msApi1 = MsApi1(
            1,
            latitude,
            longitude,
            sharedPrefUtil.getSelectedMethod().toString(),
            LocalDate().monthOfYear.toString(),
            LocalDate().year.toString()
        )

        if(msApi1.latitude.isEmpty() || msApi1.longitude.isEmpty() || msApi1.latitude == "." || msApi1.longitude == "."){
            Toasty.error(requireContext(), getString(R.string.latitude_and_longitude_cannot_be_empty), Toasty.LENGTH_SHORT).show()
            return
        }

        val arrLatitude = msApi1.latitude.toCharArray()
        val arrLongitude = msApi1.longitude.toCharArray()

        if(arrLatitude[arrLatitude.size - 1] == '.' || arrLongitude[arrLongitude.size - 1] == '.'){
            Toasty.error(requireContext(), getString(R.string.latitude_and_longitude_cannot_be_ended_with_dot), Toasty.LENGTH_SHORT).show()
            return
        }

        if(arrLatitude[0] == '.' || arrLongitude[0] == '.'){
            Toasty.error(requireContext(), getString(R.string.latitude_and_longitude_cannot_be_started_with_dot), Toasty.LENGTH_SHORT).show()
            return
        }

        viewModel.updateMsApi1(msApi1)
        Toasty.success(requireContext(), getString(R.string.success_change_the_coordinate), Toasty.LENGTH_SHORT).show()
    }

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
            setGpsComponentState(Status.ERROR)
        else
            setGpsComponentState(Status.LOADING)
    }

    @SuppressLint("MissingPermission")
    private fun onUpdateLocationListener() {
        val mLocationRequest = LocationRequest().also {
           it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
           it.interval = 60 * 1000 /* 1 minute */
           it.fastestInterval = 1 * 1000 /* 1 second */
        }
        if (!isLocationPermissionGranted()) {
            showPermissionDialog()
            return
        }
        getFusedLocationProviderClient(requireContext()).requestLocationUpdates(mLocationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation ?: return
                setGpsComponentState(Status.SUCCESS)
                dialogGpsBinding.apply {
                    tvGpsDialogLatitude.text = location.latitude.toString()
                    tvGpsDialogLongitude.text= location.longitude.toString()
                }
            }
        }, Looper.myLooper())
    }

    private fun setGpsComponentState(status: Status){
        dialogGpsBinding.apply {
            when(status){
                Status.SUCCESS -> {
                    tvViewLatitude.visibility = View.VISIBLE
                    tvViewLongitude.visibility = View.VISIBLE
                    tvGpsDialogLongitude.visibility = View.VISIBLE
                    tvGpsDialogLatitude.visibility = View.VISIBLE
                    ivWarning.visibility = View.INVISIBLE
                    tvWarning.visibility = View.INVISIBLE
                    btnProceedByGps.visibility = View.VISIBLE
                    btnProceedByGps.text = getString(R.string.proceed)
                }
                Status.LOADING -> {
                    ivWarning.visibility = View.VISIBLE
                    tvWarning.visibility = View.VISIBLE
                    tvWarning.text = getString(R.string.loading)
                    tvViewLatitude.visibility = View.INVISIBLE
                    tvViewLongitude.visibility = View.INVISIBLE
                    tvGpsDialogLongitude.visibility = View.INVISIBLE
                    tvGpsDialogLatitude.visibility = View.INVISIBLE
                    btnProceedByGps.visibility = View.INVISIBLE
                }
                Status.ERROR -> {
                    ivWarning.visibility = View.VISIBLE
                    tvWarning.visibility = View.VISIBLE
                    tvWarning.text = getString(R.string.please_enable_your_location)
                    tvGpsDialogLongitude.visibility = View.INVISIBLE
                    tvGpsDialogLatitude.visibility = View.INVISIBLE
                    tvViewLatitude.visibility = View.INVISIBLE
                    tvViewLongitude.visibility = View.INVISIBLE
                    btnProceedByGps.visibility = View.VISIBLE
                    btnProceedByGps.text = getString(R.string.open_setting)
                }
            }
        }
    }

}
