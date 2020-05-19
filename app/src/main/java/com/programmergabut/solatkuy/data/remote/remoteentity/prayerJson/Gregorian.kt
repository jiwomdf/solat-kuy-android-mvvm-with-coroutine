package com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson


import com.google.gson.annotations.SerializedName
import javax.inject.Inject

data class Gregorian @Inject constructor(
    @SerializedName("date")
    val date: String?,
    @SerializedName("day")
    var day: String,
    @SerializedName("designation")
    val designation: Designation?,
    @SerializedName("format")
    val format: String?,
    @SerializedName("month")
    val month: Month?,
    @SerializedName("weekday")
    val weekday: Weekday?,
    @SerializedName("year")
    val year: String?
)