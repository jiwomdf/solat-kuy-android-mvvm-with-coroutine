package com.programmergabut.solatkuy.data.remote.json.methodJson


import com.google.gson.annotations.SerializedName

data class EGYPT(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
)