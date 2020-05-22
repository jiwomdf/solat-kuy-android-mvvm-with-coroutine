package com.programmergabut.solatkuy.util.helper

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.util.*

class LocationHelper {

    companion object{
        fun getCity(context: Context, latitude: Double, longitude: Double): String? {
            val geoCoder = Geocoder(context, Locale.getDefault())
            var cityName: String? = null
            cityName = try {
                val addresses: List<Address> = geoCoder.getFromLocation(latitude, longitude, 1)
                addresses[0].subAdminArea
            } catch (ex: Exception){
                "-"
            }

            return cityName
        }
    }
}