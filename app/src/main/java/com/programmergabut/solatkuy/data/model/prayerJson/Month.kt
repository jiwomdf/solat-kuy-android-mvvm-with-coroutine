package com.programmergabut.solatkuy.data.model.prayerJson


import com.google.gson.annotations.SerializedName
import javax.inject.Inject

data class Month @Inject constructor(
    @SerializedName("en")
    var en: String,
    @SerializedName("number")
    val number: Int
)