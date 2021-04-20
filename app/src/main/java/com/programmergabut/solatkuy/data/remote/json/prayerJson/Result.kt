package com.programmergabut.solatkuy.data.remote.json.prayerJson


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("date")
    var date: Date,
    @SerializedName("meta")
    val meta: Meta?,
    @SerializedName("timings")
    var timings: Timings
)