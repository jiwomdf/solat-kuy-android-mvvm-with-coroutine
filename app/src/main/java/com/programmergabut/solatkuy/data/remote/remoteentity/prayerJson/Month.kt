package com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson


import com.google.gson.annotations.SerializedName
import javax.inject.Inject

data class Month @Inject constructor(
    @SerializedName("en")
    var en: String,
    @SerializedName("number")
    val number: Int
)