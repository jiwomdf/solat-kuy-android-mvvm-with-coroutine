package com.programmergabut.solatkuy.data.model.prayerApi


import com.google.gson.annotations.SerializedName

data class Month(
    @SerializedName("en")
    val en: String,
    @SerializedName("number")
    val number: Int
)