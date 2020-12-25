package com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson


import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.base.BaseResponse

class AllSurahResponse: BaseResponse() {
    @SerializedName("code")
    val code: Int = 0
    @SerializedName("data")
    lateinit var `data`: List<Data>
    @SerializedName("status")
    lateinit var status: String
}