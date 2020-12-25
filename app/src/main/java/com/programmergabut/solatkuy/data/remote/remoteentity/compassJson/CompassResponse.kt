package com.programmergabut.solatkuy.data.remote.remoteentity.compassJson


import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.base.BaseResponse

class CompassResponse: BaseResponse() {
    @SerializedName("code")
    val code: Int = 0

    @SerializedName("data")
    lateinit var `data`: Data

    @SerializedName("status")
    lateinit var status: String
}