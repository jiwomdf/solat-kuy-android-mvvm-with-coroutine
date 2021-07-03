package com.programmergabut.solatkuy.data.remote.json.prayerJson


import com.google.gson.annotations.SerializedName

data class Method(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("params")
    val params: Params
)