package com.programmergabut.solatkuy.data.remote.remoteentity.quransurahJson


import com.google.gson.annotations.SerializedName

data class QuranSurahApi(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: String
)