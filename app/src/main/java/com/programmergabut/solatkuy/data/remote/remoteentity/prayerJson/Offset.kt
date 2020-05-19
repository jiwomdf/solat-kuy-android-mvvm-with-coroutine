package com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson


import com.google.gson.annotations.SerializedName

data class Offset(
    @SerializedName("Asr")
    val asr: Int,
    @SerializedName("Dhuhr")
    val dhuhr: Int,
    @SerializedName("Fajr")
    val fajr: Int,
    @SerializedName("Imsak")
    val imsak: Int,
    @SerializedName("Isha")
    val isha: Int,
    @SerializedName("Maghrib")
    val maghrib: Int,
    @SerializedName("Midnight")
    val midnight: Int,
    @SerializedName("Sunrise")
    val sunrise: Int,
    @SerializedName("Sunset")
    val sunset: Int
)