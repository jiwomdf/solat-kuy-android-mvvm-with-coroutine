package com.programmergabut.solatkuy.data.remote.api

import com.programmergabut.solatkuy.data.remote.json.asmaalhusnaJson.AsmaAlHusnaResponse
import retrofit2.Call
import retrofit2.http.GET

interface AsmaAlHusnaService {

    //http://http://api.aladhan.com/asmaAlHusna/

    @GET("asmaAlHusna/")
    suspend fun fetchAsmaAlHusnaApi(): Call<AsmaAlHusnaResponse>

}