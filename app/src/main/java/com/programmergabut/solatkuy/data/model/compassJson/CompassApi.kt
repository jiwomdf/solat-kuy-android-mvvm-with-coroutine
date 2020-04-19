package com.programmergabut.solatkuy.data.model.compassJson


import com.google.gson.annotations.SerializedName

data class CompassApi(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: String
)