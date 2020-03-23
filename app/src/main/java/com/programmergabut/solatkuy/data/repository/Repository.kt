package com.programmergabut.solatkuy.data.repository

import androidx.lifecycle.LiveData
import com.google.gson.GsonBuilder
import com.programmergabut.solatkuy.data.api.CalendarApiService
import com.programmergabut.solatkuy.data.local.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.model.PrayerLocal
import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory

class Repository(private val notifiedPrayerDao: NotifiedPrayerDao) {

    val prayerLocal: LiveData<List<PrayerLocal>> = notifiedPrayerDao.getNotifiedPrayer()

    // Room
    suspend fun update(prayerLocal: PrayerLocal){
        notifiedPrayerDao.updateNotifiedPrayer(prayerLocal.prayerName, prayerLocal.isNotified)
    }

    //Retrofit
    private fun retrofit(): CalendarApiService{
        return Builder()
            .baseUrl("http://api.aladhan.com/v1/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(CalendarApiService::class.java)
    }

    suspend fun fetchPrayerApi(latitude:String, longitude:String): PrayerApi {
        return retrofit().fetchPrayer(latitude, longitude,8,"3","2020")
    }

}