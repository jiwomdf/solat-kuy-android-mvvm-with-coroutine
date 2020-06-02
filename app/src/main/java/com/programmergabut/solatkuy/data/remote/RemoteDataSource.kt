package com.programmergabut.solatkuy.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.ContextProviders
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.api.CalendarApiService
import com.programmergabut.solatkuy.data.remote.api.QiblaApiService
import com.programmergabut.solatkuy.data.remote.api.QuranSurahService
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassApi
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerApi
import com.programmergabut.solatkuy.data.remote.remoteentity.quransurahJson.QuranSurahApi
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

    //Coroutine
    fun fetchCompassApi(msApi1: MsApi1): LiveData<Resource<CompassApi>>{
        val result = MutableLiveData<Resource<CompassApi>>()

        CoroutineScope(contextProviders.IO).launch{
            try {
                result.postValue( Resource.success(
                    RetrofitBuilder
                    .build<QiblaApiService>(strApi)
                    .fetchQibla(msApi1.latitude, msApi1.longitude))
                )
            }
            catch (ex: Exception){
                result.postValue( Resource.error(ex.message.toString(), null))
            }
        }

        return result
    }

    /* fun fetchAsmaAlHusnaApi(): LiveData<Resource<AsmaAlHusnaApi>>{
        val result = MutableLiveData<Resource<AsmaAlHusnaApi>>()

        CoroutineScope(contextProviders.IO).launch {
            try {
                result.postValue(Resource.success(
                    RetrofitBuilder
                    .build<AsmaAlHusnaService>(strApi)
                    .fetchAsmaAlHusnaApi())
                )
            }
            catch (ex: Exception){
                result.postValue(Resource.error(ex.message.toString(), null))
            }
        }

        return result
    } */

    fun fetchPrayerApi(msApi1: MsApi1): LiveData<Resource<PrayerApi>> {
        val result = MutableLiveData<Resource<PrayerApi>>()

        GlobalScope.launch(contextProviders.IO){
            try {
                result.postValue(Resource.success(
                    RetrofitBuilder
                    .build<CalendarApiService>(strApi)
                    .fetchPrayer(msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year))
                )
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
                result.postValue(Resource.success(
                    RetrofitBuilder
                    .build<QuranSurahService>(strApi)
                    .fetchQuranSurah(nInSurah))
                )
            }
            catch (ex: Exception){
                result.postValue( Resource.error(ex.message.toString(), null))
            }
        }

        return result
    }


}