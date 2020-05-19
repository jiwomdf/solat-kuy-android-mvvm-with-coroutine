package com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson


import com.google.gson.annotations.SerializedName

data class DesignationX(
    @SerializedName("abbreviated")
    val abbreviated: String,
    @SerializedName("expanded")
    val expanded: String
)