package com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson


import com.google.gson.annotations.SerializedName

data class AllSurahApi(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("status")
    val status: String
)