package com.programmergabut.solatkuy.data.model.prayerJson


import com.google.gson.annotations.SerializedName
import javax.inject.Inject

data class WeekdayX(
    @SerializedName("ar")
    val ar: String,
    @SerializedName("en")
    val en: String
)