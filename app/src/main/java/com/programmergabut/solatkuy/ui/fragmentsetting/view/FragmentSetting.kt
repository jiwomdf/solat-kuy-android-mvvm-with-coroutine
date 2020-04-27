package com.programmergabut.solatkuy.ui.fragmentsetting.view

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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.ui.fragmentsetting.viewmodel.FragmentSettingViewModel
import com.programmergabut.solatkuy.util.LocationHelper
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.layout_bottomsheet_bygps.view.*
import kotlinx.android.synthetic.main.layout_bottomsheet_bylatitudelongitude.view.*
import org.joda.time.LocalDate

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentSetting : Fragment() {

    private lateinit var dialog: BottomSheetDialog
    lateinit var dialogView: View
    private lateinit var fragmentSettingViewModel: FragmentSettingViewModel
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val ALL_PERMISSIONS = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = BottomSheetDialog(context!!)
        btnSetLatitudeLongitude()

        fragmentSettingViewModel = ViewModelProviders.of(requireActivity()).get(FragmentSettingViewModel::class.java)
        subscribeObserversDB()
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

        fragmentSettingViewModel.updateMsApi1(data)
    }

    /* Subscribe live data */
    private fun subscribeObserversDB() {
        fragmentSettingViewModel.msApi1Local.observe(this, Observer {

            tv_view_latitude.text = it.latitude + " °S"
            tv_view_longitude.text = it.longitude + " °E"
            tv_view_city.text = LocationHelper.getCity(context!!, it.latitude.toDouble(), it.longitude.toDouble())
        })
    }


    /* init first load data */
    private fun btnSetLatitudeLongitude(){
        btn_byLatitudeLongitude.setOnClickListener {
            dialogView = layoutInflater.inflate(R.layout.layout_bottomsheet_bylatitudelongitude,null)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setContentView(dialogView)
            dialog.show()

            dialogView.btn_proceedByLL.setOnClickListener byLl@{

                val latitude = dialogView.et_llDialog_latitude.text.toString().trim()
                val longitude = dialogView.et_llDialog_longitude.text.toString().trim()

                if(latitude.isEmpty() || longitude.isEmpty() || latitude == "." || longitude == "."){
                    Toasty.warning(context!!,"latitude and longitude cannot be empty", Toast.LENGTH_SHORT).show()
                    return@byLl
                }

                val arrLatitude = latitude.toCharArray()
                val arrLongitude = longitude.toCharArray()

                if(arrLatitude[arrLatitude.size - 1] == '.' || arrLongitude[arrLongitude.size - 1] == '.'){
                    Toasty.warning(context!!,"latitude and longitude cannot be ended by .", Toast.LENGTH_SHORT).show()
                    return@byLl
                }

                if(arrLatitude[0] == '.' || arrLongitude[0] == '.'){
                    Toasty.warning(context!!,"latitude and longitude cannot be started by .", Toast.LENGTH_SHORT).show()
                    return@byLl
                }

                insertLocationSettingToDb(latitude, longitude)

                dialog.dismiss()
                Toasty.success(context!!,"Success change the coordinate", Toast.LENGTH_SHORT).show()
            }
        }

        btn_byGps.setOnClickListener{
            dialogView = layoutInflater.inflate(R.layout.layout_bottomsheet_bygps,null)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setContentView(dialogView)
            dialog.show()

            getGPSLocation(dialogView)

            dialogView.btn_proceedByGps.setOnClickListener byGps@{

                val latitude = dialogView.tv_gpsDialog_latitude.text.toString().trim()
                val longitude = dialogView.tv_gpsDialog_longitude.text.toString().trim()

                if(latitude.isEmpty() || longitude.isEmpty()){
                    Toasty.warning(context!!,"Action failed, please enable your location", Toast.LENGTH_SHORT).show()
                    return@byGps
                }

                insertLocationSettingToDb(latitude, longitude)

                dialog.dismiss()
                Toasty.success(context!!,"Success change the coordinate", Toast.LENGTH_SHORT).show()
            }
        }

        btn_seeAuthor.setOnClickListener{
            val dialog = Dialog(context!!)
            dialogView = layoutInflater.inflate(R.layout.layout_about_author,null)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setContentView(dialogView)
            dialog.show()
        }
    }

    /* permission */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == ALL_PERMISSIONS){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                onUpdateLocationListener()
            else
                Toasty.error(context!!, "All Permission Denied", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showPermissionDialog(){
        AlertDialog.Builder(context)
            .setTitle("Location Needed")
            .setMessage("Permission is needed to run the Gps")
            .setPositiveButton("ok") { _: DialogInterface, _: Int ->
                ActivityCompat.requestPermissions(
                    this.activity!!,
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
    private fun getGPSLocation(dialogView: View){

        dialogView.findViewById<TextView>(R.id.tv_view_latitude).visibility = View.GONE
        dialogView.findViewById<TextView>(R.id.tv_view_longitude).visibility = View.GONE

        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = getFusedLocationProviderClient(context!!)

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
        val lm: LocationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: java.lang.Exception) { }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: java.lang.Exception) { }

        if (!gpsEnabled && !networkEnabled)
            dialogView.tv_warning.text = "Please enable your location"
        else
            dialogView.tv_warning.text = getString(R.string.loading)
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

    private fun onUpdateLocationListener(){
        val mLocationRequest: LocationRequest?
        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 10 * 10000 /* 1 minute */
        mLocationRequest.fastestInterval = 10 * 10000 /* 1 minute */

        getFusedLocationProviderClient(context!!).requestLocationUpdates(mLocationRequest, object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {
                // do work here
                onLocationChanged(locationResult.lastLocation)
            }

            private fun onLocationChanged(it: Location?) {

                if(it == null)
                    return

                dialogView.iv_warning.visibility = View.GONE
                dialogView.tv_warning.visibility = View.GONE
                dialogView.findViewById<TextView>(R.id.tv_view_latitude).visibility = View.VISIBLE
                dialogView.findViewById<TextView>(R.id.tv_view_longitude).visibility = View.VISIBLE

                dialogView.tv_gpsDialog_latitude.text = it.latitude.toString()
                dialogView.tv_gpsDialog_longitude.text= it.longitude.toString()

            }
        }, Looper.myLooper())

    }


}
