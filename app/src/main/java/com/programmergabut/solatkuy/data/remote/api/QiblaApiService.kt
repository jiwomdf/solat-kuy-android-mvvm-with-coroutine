package com.programmergabut.solatkuy.data.remote.api

import com.programmergabut.solatkuy.data.remote.json.compassJson.CompassResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/*
 * Created by Katili Jiwo Adi Wiyono on 18/04/20.
 */

interface QiblaApiService {

    // http://api.aladhan.com/v1/qibla/7.5755/110.8243

    @GET("qibla/{latitude}/{longitude}")
    fun fetchQibla(@Path("latitude") latitude: String,
                       @Path("longitude") longitude: String): Call<CompassResponse>
}