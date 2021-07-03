package com.programmergabut.solatkuy.data.remote.json.compassJson


import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.base.BaseResponse

class CompassResponse: BaseResponse() {
    @SerializedName("data")
    lateinit var `data`: Result
}