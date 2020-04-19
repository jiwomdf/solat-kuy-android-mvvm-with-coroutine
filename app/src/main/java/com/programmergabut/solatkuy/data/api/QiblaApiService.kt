package com.programmergabut.solatkuy.data.api

import com.programmergabut.solatkuy.data.model.compassJson.CompassApi
import com.programmergabut.solatkuy.data.model.prayerJson.PrayerApi
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/*
 * Created by Katili Jiwo Adi Wiyono on 18/04/20.
 */

interface QiblaApiService {

    // http://api.aladhan.com/v1/qibla/7.5755/110.8243

    @GET("qibla/{latitude}/{longitude}")
    suspend fun fetchQibla(@Path("latitude") latitude: String,
                           @Path("longitude") longitude: String): CompassApi
}