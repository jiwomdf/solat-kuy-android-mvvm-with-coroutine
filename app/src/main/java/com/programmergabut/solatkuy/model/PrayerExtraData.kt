package com.programmergabut.solatkuy.model

import android.os.Bundle
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PrayerExtraData(
    val prayerId: Int,
    val prayerName: String,
    val prayerTime: String,
    val prayerCity: String,
    val listPrayerBundle: Bundle?
): Parcelable