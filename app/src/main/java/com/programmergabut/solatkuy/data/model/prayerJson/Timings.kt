package com.programmergabut.solatkuy.data.model.prayerJson


import com.google.gson.annotations.SerializedName

data class Timings(
    @SerializedName("Asr")
    var asr: String,
    @SerializedName("Dhuhr")
    var dhuhr: String,
    @SerializedName("Fajr")
    var fajr: String,
    @SerializedName("Imsak")
    val imsak: String,
    @SerializedName("Isha")
    var isha: String,
    @SerializedName("Maghrib")
    var maghrib: String,
    @SerializedName("Midnight")
    val midnight: String,
    @SerializedName("Sunrise")
    val sunrise: String,
    @SerializedName("Sunset")
    val sunset: String
)