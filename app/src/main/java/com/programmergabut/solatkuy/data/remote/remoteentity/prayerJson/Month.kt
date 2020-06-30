package com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson


import com.google.gson.annotations.SerializedName

data class Month(
    @SerializedName("en")
    var en: String,
    @SerializedName("number")
    val number: Int
)