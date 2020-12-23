package com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson


import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.base.BaseResponse

data class AsmaAlHusnaResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("status")
    val status: String
): BaseResponse()