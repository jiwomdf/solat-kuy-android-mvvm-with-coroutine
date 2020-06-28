package com.programmergabut.solatkuy.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.api.CalendarApiService
import com.programmergabut.solatkuy.data.remote.api.QiblaApiService
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/*
 * Created by Katili Jiwo Adi Wiyono on 09/05/20.
 */

class RemoteDataSourceAladhan constructor(){

    private val strApi = "https://api.aladhan.com/v1/"

    companion object{
        @Volatile
        private var instance: RemoteDataSourceAladhan? = null

        fun getInstance() = instance
            ?: synchronized(this){
            instance
                ?: RemoteDataSourceAladhan()
        }
    }

    //Coroutine
    suspend fun fetchCompassApi(msApi1: MsApi1): Response<CompassResponse> {

        return RetrofitBuilder
                .build<QiblaApiService>(strApi)
                .fetchQibla(msApi1.latitude, msApi1.longitude)
    }

    /* fun fetchAsmaAlHusnaApi(): LiveData<Resource<AsmaAlHusnaApi>>{
        val result = MutableLiveData<Resource<AsmaAlHusnaApi>>()

        _root_ide_package_.kotlinx.coroutines.GlobalScope(contextProviders.IO).launch {
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

    suspend fun fetchPrayerApi(msApi1: MsApi1): Response<PrayerResponse> {

        return RetrofitBuilder
                    .build<CalendarApiService>(strApi)
                    .fetchPrayer(msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)


        /*val result = MutableLiveData<Resource<PrayerResponse>>()
        result.postValue(Resource.loading(null))

        GlobalScope.launch(Dispatchers.IO) {
            try {
                result.postValue(Resource.success(
                    RetrofitBuilder
                        .build<CalendarApiService>(strApi)
                        .fetchPrayer(msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year))
                )
            }
            catch (ex: Exception){
                result.postValue(Resource.error(ex.message.toString(), null))
            }
        }

        return result */
    }

}