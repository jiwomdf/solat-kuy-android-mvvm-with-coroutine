package com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr


import com.google.gson.annotations.SerializedName

data class ReadSurahArApi(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: String
)