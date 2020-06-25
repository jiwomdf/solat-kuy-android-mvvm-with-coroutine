package com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson


import com.google.gson.annotations.SerializedName
import javax.inject.Inject

data class Designation(
    @SerializedName("abbreviated")
    val abbreviated: String,
    @SerializedName("expanded")
    val expanded: String
)