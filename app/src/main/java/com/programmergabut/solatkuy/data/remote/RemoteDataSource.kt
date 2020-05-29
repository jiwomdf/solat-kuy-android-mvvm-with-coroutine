package com.programmergabut.solatkuy.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.programmergabut.solatkuy.data.ContextProviders
import com.programmergabut.solatkuy.data.remote.api.AsmaAlHusnaService
import com.programmergabut.solatkuy.data.remote.api.CalendarApiService
import com.programmergabut.solatkuy.data.remote.api.QiblaApiService
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.api.QuranSurahService
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.AsmaAlHusnaApi
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassApi
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerApi
import com.programmergabut.solatkuy.data.remote.remoteentity.quransurahJson.QuranSurahApi
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

/*
 * Created by Katili Jiwo Adi Wiyono on 09/05/20.
 */

class RemoteDataSource(private val contextProviders: ContextProviders) {

    private val strApi = "https://api.aladhan.com/v1/"

    companion object{
        @Volatile
        private var instance: RemoteDataSource? = null

        fun getInstance(contextProviders: ContextProviders) = instance
            ?: synchronized(this){
            instance
                ?: RemoteDataSource(contextProviders)
        }
    }

    //Service Builder
    private fun getQiblaApi(): QiblaApiService {
        return Retrofit.Builder()
            .baseUrl(strApi)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(QiblaApiService::class.java)
    }

    private fun getAsmaAlHusnaApi(): AsmaAlHusnaService {
        return Retrofit.Builder()
            .baseUrl(strApi)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(AsmaAlHusnaService::class.java)
    }

    private fun getCalendarApi(): CalendarApiService {
        return Retrofit.Builder()
            .baseUrl(strApi)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(CalendarApiService::class.java)
    }

    private fun getQuranSurahApi(): QuranSurahService {
        return Retrofit.Builder()
            .baseUrl(strApi)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(QuranSurahService::class.java)
    }

    //Coroutine
    fun fetchCompassApi(msApi1: MsApi1): LiveData<Resource<CompassApi>>{
        val result = MutableLiveData<Resource<CompassApi>>()

        CoroutineScope(contextProviders.IO).launch{
            try {
                result.postValue( Resource.success(getQiblaApi().fetchQibla(msApi1.latitude, msApi1.longitude)))
            }
            catch (ex: Exception){
                result.postValue( Resource.error(ex.message.toString(), null))
            }
        }

        return result
    }

    fun fetchAsmaAlHusnaApi(): LiveData<Resource<AsmaAlHusnaApi>>{
        val result = MutableLiveData<Resource<AsmaAlHusnaApi>>()

        CoroutineScope(contextProviders.IO).launch {
            try {
                result.postValue(Resource.success(getAsmaAlHusnaApi().fetchAsmaAlHusnaApi()))
            }
            catch (ex: Exception){
                result.postValue(Resource.error(ex.message.toString(), null))
            }
        }

        return result
    }

    fun syncNotifiedPrayer(msApi1: MsApi1): MutableLiveData<Resource<PrayerApi>> {
        val resultContent = MutableLiveData<Resource<PrayerApi>>()

        GlobalScope.launch(contextProviders.IO){
            try {
                resultContent.postValue(Resource.success(getCalendarApi()
                    .fetchPrayer( msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)))
            }
            catch (ex: Exception){
                Resource.error(ex.message.toString(), null)
            }
        }

        return resultContent
    }

    fun fetchPrayerApi(msApi1: MsApi1): LiveData<Resource<PrayerApi>> {
        val result = MutableLiveData<Resource<PrayerApi>>()

        GlobalScope.launch(contextProviders.IO){
            try {
                result.postValue( Resource.success(getCalendarApi()
                    .fetchPrayer( msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)))
            }
            catch (ex: Exception){
                result.postValue( Resource.error(ex.message.toString(), null))
            }
        }

        return result
    }

    fun fetchQuranSurah(nInSurah: String): MutableLiveData<Resource<QuranSurahApi>> {
        val result = MutableLiveData<Resource<QuranSurahApi>>()

        GlobalScope.launch(contextProviders.IO){
            try {
                result.postValue( Resource.success(getQuranSurahApi()
                    .fetchQuranSurah(nInSurah)))
            }
            catch (ex: Exception){
                result.postValue( Resource.error(ex.message.toString(), null))
            }
        }

        return result
    }

}