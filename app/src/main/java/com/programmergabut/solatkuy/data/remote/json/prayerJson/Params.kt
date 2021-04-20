package com.programmergabut.solatkuy.data.remote.json.prayerJson


import com.google.gson.annotations.SerializedName

data class Params(
    @SerializedName("Fajr")
    val fajr: Double,
    @SerializedName("Isha")
    val isha: String
)