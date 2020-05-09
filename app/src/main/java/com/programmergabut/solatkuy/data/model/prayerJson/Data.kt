package com.programmergabut.solatkuy.data.model.prayerJson


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("date")
    var date: Date,
    @SerializedName("meta")
    val meta: Meta?,
    @SerializedName("timings")
    var timings: Timings
)