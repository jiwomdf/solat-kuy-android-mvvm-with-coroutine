package com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn


import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.base.BaseResponse

data class ReadSurahEnResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: String
): BaseResponse()