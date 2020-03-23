package com.programmergabut.solatkuy.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.programmergabut.solatkuy.data.api.ApiServiceImpl
import com.programmergabut.solatkuy.data.local.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.model.PrayerLocal
import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory

class RepositoryLocal(private val notifiedPrayerDao: NotifiedPrayerDao) {

    val prayerLocal: LiveData<List<PrayerLocal>> = notifiedPrayerDao.getNotifiedPrayer()

    // Room
    suspend fun update(prayerLocal: PrayerLocal){
        notifiedPrayerDao.updateNotifiedPrayer(prayerLocal.prayerName, prayerLocal.isNotified)
    }

    //Retrofit
    private fun retrofit(): ApiServiceImpl{
        return Builder()
            .baseUrl("http://api.aladhan.com/v1/")
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ApiServiceImpl::class.java)
    }

    suspend fun fetchPrayerApi(latitude:String, longitude:String): PrayerApi{
        return retrofit().fetchPrayer(latitude, longitude,8,"3","2020")
    }

}