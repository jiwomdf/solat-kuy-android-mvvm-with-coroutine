package com.programmergabut.solatkuy.data.api

import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceImpl {

    @GET("calendar?latitude={latitude}&longitude={longitude}&method={method}&month={month}&year={year}")
    suspend fun fetchPrayer(@Path("latitude") latitude: String,
                            @Path("longitude") longitude: String,
                            @Path("method") method: Int,
                            @Path("month") month: String,
                            @Path("year") year: String): PrayerApi
}