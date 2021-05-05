package com.programmergabut.solatkuy.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PrayerListExtraData(
    val listPrayerID: ArrayList<Int>,
    val listPrayerName: ArrayList<String>,
    val listPrayerTime: ArrayList<String>,
    val listPrayerIsNotified: ArrayList<Int>,
    val listPrayerCity: ArrayList<String>
): Parcelable