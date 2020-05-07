package com.programmergabut.solatkuy.data

import com.google.gson.GsonBuilder
import com.programmergabut.solatkuy.data.api.AsmaAlHusnaService
import com.programmergabut.solatkuy.data.api.CalendarApiService
import com.programmergabut.solatkuy.data.api.QiblaApiService
import com.programmergabut.solatkuy.data.model.asmaalhusnaJson.AsmaAlHusnaApi
import com.programmergabut.solatkuy.data.model.compassJson.CompassApi
import com.programmergabut.solatkuy.data.model.prayerJson.PrayerApi
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class RemoteDataSource {

    private val strApi = "https://api.aladhan.com/v1/"

    companion object{
        @Volatile
        private var instance: RemoteDataSource? = null

        fun getInstance() = instance ?: synchronized(this){
            instance ?: RemoteDataSource()
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

    //Coroutine
    fun fetchCompassApi(callback: LoadCompassCallback, latitude:String, longitude:String){

        CoroutineScope(Dispatchers.IO).launch{
            try {
                callback.onReceived(
                    Resource.success(getQiblaApi().fetchQibla(latitude, longitude)
                ))
            }
            catch (ex: Exception){
                callback.onReceived(Resource.error(ex.message.toString(), null))
            }
        }
    }

    fun fetchAsmaAlHusnaApi(callback: LoadAsmaAlHusnaCallback){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                callback.onReceived(Resource.success(getAsmaAlHusnaApi().fetchAsmaAlHusnaApi()))
            }
            catch (ex: Exception){
                callback.onReceived(Resource.error(ex.message.toString(), null))
            }
        }
    }

    fun fetchPrayerApi(callback: LoadPrayerCallback, latitude: String, longitude: String, method: String, month: String, year: String){

        CoroutineScope(Dispatchers.IO).launch{
            try {
                callback.onReceived(Resource.success(getCalendarApi()
                    .fetchPrayer(latitude, longitude, method, month, year)))
            }
            catch (ex: Exception){
                callback.onReceived(Resource.error(ex.message.toString(), null))
            }
        }
    }

    //Internal interface
    interface LoadCompassCallback{
        fun onReceived(response: Resource<CompassApi>)
    }

    interface LoadAsmaAlHusnaCallback{
        fun onReceived(response: Resource<AsmaAlHusnaApi>)
    }

    interface LoadPrayerCallback{
        fun onReceived(response: Resource<PrayerApi>)
    }

}