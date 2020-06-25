package com.programmergabut.solatkuy.data.remote.remoteentity.compassJson


import com.google.gson.annotations.SerializedName

data class CompassResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: String
)