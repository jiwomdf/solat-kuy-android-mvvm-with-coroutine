package com.programmergabut.solatkuy.data.model.asmaalhusnaJson


import com.google.gson.annotations.SerializedName

data class AsmaAlHusnaApi(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("status")
    val status: String
)