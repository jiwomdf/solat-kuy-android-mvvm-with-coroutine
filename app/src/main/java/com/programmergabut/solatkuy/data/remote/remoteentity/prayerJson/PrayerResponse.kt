package com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson


import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.base.BaseResponse

class PrayerResponse: BaseResponse() {
    @SerializedName("code")
    var code: Int = 0

    @SerializedName("data")
    lateinit var `data`: List<Data>

    @SerializedName("status")
    lateinit var status: String
}