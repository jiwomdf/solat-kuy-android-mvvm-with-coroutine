package com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson


import com.google.gson.annotations.SerializedName
import javax.inject.Named

data class Timings(
    @SerializedName("Asr")
    @Named("img_asr")
    var asr: String,

    @SerializedName("Dhuhr")
    @Named("img_dhuhr")
    var dhuhr: String,

    @SerializedName("Fajr")
    @Named("img_fajr")
    var fajr: String,

    @SerializedName("Imsak")
    @Named("imsak")
    val imsak: String,

    @SerializedName("Isha")
    @Named("img_isha")
    var isha: String,

    @SerializedName("Maghrib")
    @Named("img_maghrib")
    var maghrib: String,

    @SerializedName("Midnight")
    @Named("midnight")
    val midnight: String,

    @SerializedName("Sunrise")
    @Named("img_sunrise")
    val sunrise: String,

    @SerializedName("Sunset")
    @Named("sunset")
    val sunset: String
)