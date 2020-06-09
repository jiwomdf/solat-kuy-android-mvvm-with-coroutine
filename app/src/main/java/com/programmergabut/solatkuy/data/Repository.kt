package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.programmergabut.solatkuy.data.local.LocalDataSource
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.RemoteDataSource
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerApi
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

class Repository(private val contextProviders: ContextProviders,
                 private val remoteDataSource: RemoteDataSource,
                 private val localDataSource: LocalDataSource) {

    companion object{
        @Volatile
        private var instance: Repository? = null

        fun getInstance(contextProviders: ContextProviders,
                        remoteDataSource: RemoteDataSource, localDataSource: LocalDataSource) =
            instance ?: synchronized(this){
                instance
                    ?: Repository(contextProviders, remoteDataSource, localDataSource)
            }
    }

    //Room
    fun getMsApi1() = localDataSource.getMsApi1()

    //fun getMsApi1() = localDataSource.getMsApi1()
    fun getMsSetting() = localDataSource.getMsSetting()

    //fun updateNotifiedPrayer(notifiedPrayer: NotifiedPrayer) = localDataSource.updateNotifiedPrayer(notifiedPrayer)
    //fun updatePrayerTime(prayerName: String, prayerTime: String) = localDataSource.updatePrayerTime(prayerName, prayerTime)
    fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = localDataSource.updatePrayerIsNotified(prayerName, isNotified)
    fun updateMsApi1(msApi1: MsApi1) = localDataSource.updateMsApi1(msApi1)
    //fun updateMsSetting(isHasOpen: Boolean) = localDataSource.updateMsSetting(isHasOpen)

    //Retrofit
    fun fetchCompass(msApi1: MsApi1) = remoteDataSource.fetchCompassApi(msApi1)

    //fun fetchAsmaAlHusna() = remoteDataSource.fetchAsmaAlHusnaApi()

    fun fetchPrayerApi(msApi1: MsApi1) = remoteDataSource.fetchPrayerApi(msApi1)

    fun fetchQuranSurah(nInSurah: String) = remoteDataSource.fetchQuranSurah(nInSurah)

    fun syncNotifiedPrayer(msApi1: MsApi1): LiveData<Resource<List<NotifiedPrayer>>> {

        return object : NetworkBoundResource<List<NotifiedPrayer>, PrayerApi>(contextProviders){
            override fun loadFromDB(): LiveData<List<NotifiedPrayer>> = localDataSource.getNotifiedPrayer()

            override fun shouldFetch(data: List<NotifiedPrayer>?): Boolean = true

            override fun createCall(): LiveData<Resource<PrayerApi>> = remoteDataSource.fetchPrayerApi(msApi1)

            override fun saveCallResult(data: PrayerApi) {

                val sdf = SimpleDateFormat("dd", Locale.getDefault())
                val currentDate = sdf.format(Date())

                val timings = data.data.find { obj -> obj.date.gregorian?.day == currentDate.toString() }?.timings

                val map = mutableMapOf<String, String>()

                map[EnumConfig.fajr] = timings?.fajr.toString()
                map[EnumConfig.dhuhr] = timings?.dhuhr.toString()
                map[EnumConfig.asr] = timings?.asr.toString()
                map[EnumConfig.maghrib] = timings?.maghrib.toString()
                map[EnumConfig.isha] = timings?.isha.toString()
                map[EnumConfig.sunrise] = timings?.sunrise.toString()

                GlobalScope.launch(contextProviders.IO){
                    map.forEach { p ->
                        localDataSource.updatePrayerTime(p.key, p.value)
                    }
                }

            }
        }.asLiveData()
    }

}