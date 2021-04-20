package com.programmergabut.solatkuy.data.remote.json.readsurahJsonAr


import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.base.BaseResponse

class ReadSurahArResponse: BaseResponse() {
    @SerializedName("data")
    lateinit var `data`: Result
}