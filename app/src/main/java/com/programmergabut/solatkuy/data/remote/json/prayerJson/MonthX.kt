package com.programmergabut.solatkuy.data.remote.json.prayerJson


import com.google.gson.annotations.SerializedName

data class MonthX(
    @SerializedName("ar")
    val ar: String,
    @SerializedName("en")
    val en: String,
    @SerializedName("number")
    val number: Int
)