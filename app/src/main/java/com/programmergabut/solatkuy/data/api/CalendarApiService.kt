package com.programmergabut.solatkuy.data.api

import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CalendarApiService {

    //http://api.aladhan.com/v1/calendar?latitude=-7.55611&longitude=110.83167&method=8&month=3&year=2020

    @GET("calendar?")
    suspend fun fetchPrayer(@Query("latitude") latitude: String,
                            @Query("longitude") longitude: String,
                            @Query("method") method: String,
                            @Query("month") month: String,
                            @Query("year") year: String): PrayerApi
}