package com.programmergabut.solatkuy.data.model.compassJson


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("direction")
    val direction: Double,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)