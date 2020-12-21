package com.programmergabut.solatkuy.ui.fragmentsetting

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
    R.layout.fragment_setting, FragmentSettingViewModel::class.java, viewModelTest
) {

    private lateinit var dialog: BottomSheetDialog
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val ALL_PERMISSIONS = 101

    private lateinit var dialogGpsBinding: LayoutBottomsheetBygpsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = BottomSheetDialog(requireContext())
        inflateBinding()
        subscribeObserversDB()
        observeErrorMsg()
    }

    override fun setListener() {
        super.setListener()
        btnSetLatitudeLongitude()
    }

    private fun inflateBinding() {
        dialogGpsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.layout_bottomsheet_bygps, null, true
        )
    }

    private fun subscribeObserversDB() {
        viewModel.msApi1.observe(viewLifecycleOwner, {
            when(it.status){
                EnumStatus.SUCCESS -> {
                    val retVal = it.data
                    if(retVal != null){
                        val city = LocationHelper.getCity(requireContext(), retVal.latitude.toDouble(), retVal.longitude.toDouble())
                        binding.tvViewLatitude.text = retVal.latitude + " °S"
                        binding.tvViewLongitude.text = retVal.longitude + " °E"
                        binding.tvViewCity.text = city ?: EnumConfig.CITY_NOT_FOUND_STR
                    }
                }
                else -> {/*NO-OP*/}
            }
        })
    }

    private fun observeErrorMsg() {
        viewModel.errStatus.observe(viewLifecycleOwner, {
            val errMsg = viewModel.getErrMsg()

            if(errMsg.isEmpty())
                return@observe

            when(it){
                EnumStatus.SUCCESS -> {
                    Toasty.success(requireContext(),errMsg , Toasty.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                EnumStatus.ERROR -> {
                    Toasty.error(requireContext(), errMsg, Toasty.LENGTH_SHORT).show()
                }
                else -> {/*NO-OP*/}
            }
        })
    }

    /* init first load data */
    private fun btnSetLatitudeLongitude(){

        binding.btnByLatitudeLongitude.setOnClickListener {
            val dialogLatLngBinding = DataBindingUtil.inflate<LayoutBottomsheetBylatitudelongitudeBinding>(
                LayoutInflater.from(requireContext()),
                R.layout.layout_bottomsheet_bylatitudelongitude, null, true
            )
            dialog.setContentView(dialogLatLngBinding.root)
            dialog.show()

            dialogLatLngBinding.btnProceedByLL.setOnClickListener{
                val latitude = dialogLatLngBinding.etLlDialogLatitude.text.toString().trim()
                val longitude = dialogLatLngBinding.etLlDialogLongitude.text.toString().trim()

                insertLocationSettingToDb(latitude, longitude)
            }
        }

        binding.btnByGps.setOnClickListener{
            dialog.setContentView(dialogGpsBinding.root)
            dialog.show()

            getGPSLocation(dialogGpsBinding)

            dialogGpsBinding.btnProceedByGps.setOnClickListener{
                val latitude = dialogGpsBinding.tvGpsDialogLatitude.text.toString().trim()
                val longitude = dialogGpsBinding.tvGpsDialogLongitude.text.toString().trim()

                insertLocationSettingToDb(latitude, longitude)
            }
        }

        binding.btnSeeAuthor.setOnClickListener{
            val dialog = Dialog(requireContext())
            val dialogAuthorBinding = DataBindingUtil.inflate<LayoutAboutAuthorBinding>(
                LayoutInflater.from(requireContext()),
                R.layout.layout_about_author, null, true
            )
            dialog.setContentView(dialogAuthorBinding.root)
            dialog.show()
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

        if(requestCode == ALL_PERMISSIONS){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                onUpdateLocationListener(dialogGpsBinding)
            else
                Toasty.error(requireContext(), "All Permission Denied", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showPermissionDialog(){
        AlertDialog.Builder(context)
            .setTitle("Location Needed")
            .setMessage("Permission is needed to run the Gps")
            .setPositiveButton("ok") { _: DialogInterface, _: Int ->
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    ALL_PERMISSIONS
                )
            }
            .setNegativeButton("cancel") { _: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    /* supporting function */
    private fun getGPSLocation(dialogGpsBinding: LayoutBottomsheetBygpsBinding){

        dialogGpsBinding.tvViewLatitude.visibility = View.GONE
        dialogGpsBinding.tvViewLongitude.visibility = View.GONE

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = getFusedLocationProviderClient(requireContext())

            //onSuccessListener()
            onFailedListener(dialogGpsBinding)
            onUpdateLocationListener(dialogGpsBinding)
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

    private fun onFailedListener(dialogGpsBinding: LayoutBottomsheetBygpsBinding) {
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
            dialogGpsBinding.tvWarning.text = getString(R.string.please_enable_your_location)
        else
            dialogGpsBinding.tvWarning.text = getString(R.string.loading)
    }

    /* private fun onSuccessListener(){
        mFusedLocationClient.lastLocation.addOnSuccessListener {
            if(it == null)
                return@addOnSuccessListener

            dialogView.iv_warning.visibility = View.GONE
            dialogView.tv_warning.visibility = View.GONE
            dialogView.findViewById<TextView>(R.id.tv_view_latitude).visibility = View.VISIBLE
            dialogView.findViewById<TextView>(R.id.tv_view_longitude).visibility = View.VISIBLE

            dialogView.tv_gpsDialog_latitude.text = it.latitude.toString()
            dialogView.tv_gpsDialog_longitude.text= it.longitude.toString()
        }
    }*/

    private fun onUpdateLocationListener(dialogGpsBinding: LayoutBottomsheetBygpsBinding) {
        val mLocationRequest: LocationRequest?
        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 10 * 10000 /* 1 minute */
        mLocationRequest.fastestInterval = 10 * 10000 /* 1 minute */

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        getFusedLocationProviderClient(requireContext()).requestLocationUpdates(mLocationRequest, object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {
                onLocationChanged(locationResult.lastLocation)
            }

            private fun onLocationChanged(it: Location?) {

                if(it == null) return

                dialogGpsBinding.ivWarning.visibility = View.GONE
                dialogGpsBinding.tvWarning.visibility = View.GONE
                dialogGpsBinding.tvViewLatitude.visibility = View.VISIBLE
                dialogGpsBinding.tvViewLongitude.visibility = View.VISIBLE

                dialogGpsBinding.tvGpsDialogLatitude.text = it.latitude.toString()
                dialogGpsBinding.tvGpsDialogLongitude.text= it.longitude.toString()

            }
        }, Looper.myLooper())

    }


}
