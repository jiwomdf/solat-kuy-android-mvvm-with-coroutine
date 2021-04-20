package com.programmergabut.solatkuy.data.remote.json.quranallsurahJson


import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.base.BaseResponse

class AllSurahResponse: BaseResponse() {
    @SerializedName("data")
    lateinit var `data`: List<Result>
}