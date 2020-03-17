package com.programmergabut.solatkuy.data.model.prayerApi


import com.google.gson.annotations.SerializedName

data class DesignationX(
    @SerializedName("abbreviated")
    val abbreviated: String,
    @SerializedName("expanded")
    val expanded: String
)