package com.programmergabut.solatkuy.data.remote.json.prayerJson


import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.base.BaseResponse

class PrayerResponse: BaseResponse() {
    @SerializedName("data")
    lateinit var `data`: List<Result>
}