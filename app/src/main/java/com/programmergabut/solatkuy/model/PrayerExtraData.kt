package com.programmergabut.solatkuy.model

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PrayerExtraData(
    var prayerId: Int = 0,
    var prayerName: String? = null,
    var prayerTime: String? = null,
    var prayerCity: String? = null
): Parcelable