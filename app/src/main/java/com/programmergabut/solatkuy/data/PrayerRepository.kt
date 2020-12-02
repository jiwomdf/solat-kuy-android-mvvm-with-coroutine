package com.programmergabut.solatkuy.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhanImpl
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquranImpl
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

class PrayerRepository @Inject constructor(
    private val remoteDataSourceAladhanImpl: RemoteDataSourceAladhanImpl,
    private val notifiedPrayerDao: NotifiedPrayerDao,
    private val msApi1Dao: MsApi1Dao,
    private val msSettingDao: MsSettingDao,
) {

    /* Room */
    /* NotifiedPrayer */
    suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = notifiedPrayerDao.updatePrayerIsNotified(prayerName, isNotified)

    /* MsApi1 */
    fun getMsApi1(): LiveData<Resource<MsApi1>> {
        val data = MediatorLiveData<Resource<MsApi1>>()
        val msApi1 = msApi1Dao.getMsApi1()

        data.value = Resource.loading(null)

        data.addSource(msApi1) {
            data.value = Resource.success(it)
        }

        return data
    }
    suspend fun updateMsApi1(msApi1: MsApi1) = msApi1Dao.updateMsApi1(msApi1.api1ID, msApi1.latitude,
        msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)

    /* MsSetting */
    fun getMsSetting(): LiveData<Resource<MsSetting>> {
        val data = MediatorLiveData<Resource<MsSetting>>()
        val msSetting = msSettingDao.getMsSetting()

        data.value = Resource.loading(null)

        data.addSource(msSetting) {
            data.value = Resource.success(it)
        }

        return data
    }
    suspend fun updateIsUsingDBQuotes(isUsingDBQuotes: Boolean) = msSettingDao.updateIsUsingDBQuotes(isUsingDBQuotes)
    suspend fun updateMsApi1MonthAndYear(api1ID: Int, month: String, year:String) = msApi1Dao.updateMsApi1MonthAndYear(api1ID, month, year)
    suspend fun updateIsHasOpenApp(isHasOpen: Boolean) = msSettingDao.updateIsHasOpenApp(isHasOpen)

    /*
     * Retrofit
     */
    suspend fun fetchCompass(msApi1: MsApi1) = remoteDataSourceAladhanImpl.fetchCompassApi(msApi1)
    suspend fun fetchPrayerApi(msApi1: MsApi1) = remoteDataSourceAladhanImpl.fetchPrayerApi(msApi1)
    suspend fun syncNotifiedPrayer(msApi1: MsApi1): List<NotifiedPrayer> {

        try {
            val data = remoteDataSourceAladhanImpl.fetchPrayerApi(msApi1)
            Log.d("syncNotifiedPrayer", "fetch")

            data.let {
                val sdf = SimpleDateFormat("dd", Locale.getDefault())
                val currentDate = sdf.format(Date())

                val timings =
                    it.data.find { obj -> obj.date.gregorian?.day == currentDate.toString() }?.timings

                val map = mutableMapOf<String, String>()

                map[EnumConfig.FAJR] = timings?.fajr.toString()
                map[EnumConfig.DHUHR] = timings?.dhuhr.toString()
                map[EnumConfig.ASR] = timings?.asr.toString()
                map[EnumConfig.MAGHRIB] = timings?.maghrib.toString()
                map[EnumConfig.ISHA] = timings?.isha.toString()
                map[EnumConfig.SUNRISE] = timings?.sunrise.toString()

                map.forEach { p ->
                    notifiedPrayerDao.updatePrayerTime(p.key, p.value)
                    Log.d("syncNotifiedPrayer", "updated")
                }
            }
        }
        catch (ex :Exception){
            print("not connected to internet and using the offline data")
        }

        return notifiedPrayerDao.getListNotifiedPrayerSync()

        /* return object : NetworkBoundResource<List<NotifiedPrayer>, PrayerResponse>(){
            override fun loadFromDB(): LiveData<List<NotifiedPrayer>> = notifiedPrayerDao.getNotifiedPrayer()

            override fun shouldFetch(data: List<NotifiedPrayer>?): Boolean = true

            override fun createCall(): LiveData<Resource<PrayerResponse>> = remoteDataSourceAladhan.fetchPrayerApi(msApi1)

            override fun saveCallResult(data: PrayerResponse){
                val sdf = SimpleDateFormat("dd", Locale.getDefault())
                val currentDate = sdf.format(Date())

                val timings = data.data.find { obj -> obj.date.gregorian?.day == currentDate.toString() }?.timings

                val map = mutableMapOf<String, String>()

                map[EnumConfig.img_fajr] = timings?.img_fajr.toString()
                map[EnumConfig.img_dhuhr] = timings?.img_dhuhr.toString()
                map[EnumConfig.img_asr] = timings?.img_asr.toString()
                map[EnumConfig.img_maghrib] = timings?.img_maghrib.toString()
                map[EnumConfig.img_isha] = timings?.img_isha.toString()
                map[EnumConfig.img_sunrise] = timings?.img_sunrise.toString()

                CoroutineScope(Dispatchers.IO).launch {
                    map.forEach { p ->
                        notifiedPrayerDao.updatePrayerTime(p.key, p.value)
                    }
                }
            }
        }.asLiveData() */
    }
}

