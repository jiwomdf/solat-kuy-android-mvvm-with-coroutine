package com.programmergabut.solatkuy.ui

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.util.*

object LocationHelper {
    fun getCity(context: Context, latitude: Double, longitude: Double): String? {
        return try {
            val addresses: List<Address> = Geocoder(context,
                Locale.getDefault()).getFromLocation(latitude, longitude, 1)
            addresses[0].subAdminArea
        } catch (ex: Exception){
            "-"
        }
    }
}