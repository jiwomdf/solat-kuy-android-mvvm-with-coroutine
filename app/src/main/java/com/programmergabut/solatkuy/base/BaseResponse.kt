package com.programmergabut.solatkuy.base

import com.google.gson.annotations.SerializedName

abstract class BaseResponse {
    var responseStatus : String = ""
    var message : String = ""
    @SerializedName("code")
    var code: Int = 0
    @SerializedName("status")
    lateinit var status: String
}