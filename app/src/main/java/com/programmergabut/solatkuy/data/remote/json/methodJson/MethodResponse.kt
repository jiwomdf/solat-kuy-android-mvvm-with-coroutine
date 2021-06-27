package com.programmergabut.solatkuy.data.remote.json.methodJson


import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.base.BaseResponse

class MethodResponse : BaseResponse() {
    @SerializedName("data")
    lateinit var `data`: MethodData
}