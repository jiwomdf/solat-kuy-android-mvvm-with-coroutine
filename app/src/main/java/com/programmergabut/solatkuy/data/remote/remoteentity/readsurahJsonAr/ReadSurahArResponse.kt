package com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr


import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.base.BaseResponse

class ReadSurahArResponse: BaseResponse() {
    @SerializedName("code")
    var code: Int = 0
    @SerializedName("data")
    lateinit var `data`: Data
    @SerializedName("status")
    lateinit var status: String
}