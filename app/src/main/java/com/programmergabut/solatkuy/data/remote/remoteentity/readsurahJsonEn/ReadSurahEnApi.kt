package com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn


import com.google.gson.annotations.SerializedName

data class ReadSurahEnApi(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: String
)