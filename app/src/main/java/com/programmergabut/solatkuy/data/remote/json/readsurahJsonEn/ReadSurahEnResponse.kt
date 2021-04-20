package com.programmergabut.solatkuy.data.remote.json.readsurahJsonEn


import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.base.BaseResponse

class ReadSurahEnResponse: BaseResponse() {
    @SerializedName("data")
    lateinit var `data`: Result
}