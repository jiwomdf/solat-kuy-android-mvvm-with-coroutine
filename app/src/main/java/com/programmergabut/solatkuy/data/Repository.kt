package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.local.LocalDataSource
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhan
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquran
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

class Repository constructor(
    val remoteDataSourceAladhan: RemoteDataSourceAladhan,
    val remoteDataSourceApiAlquran: RemoteDataSourceApiAlquran,
    val notifiedPrayerDao: NotifiedPrayerDao,
    val msApi1Dao: MsApi1Dao,
    val msSettingDao: MsSettingDao,
    val msFavAyahDao: MsFavAyahDao,
    val msFavSurahDao: MsFavSurahDao
) {

    /* companion object{
        @Volatile
        private var instance: Repository? = null

        fun getInstance(remoteDataSourceAladhan: RemoteDataSourceAladhan,
                        remoteDataSourceApiAlquran: RemoteDataSourceApiAlquran,
                        localDataSource: LocalDataSource) =
            instance ?: synchronized(this){
                instance
                    ?: Repository(remoteDataSourceAladhan, remoteDataSourceApiAlquran, localDataSource)
            }
    } */

    /* Room */
    /* NotifiedPrayer */
    suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = notifiedPrayerDao.updatePrayerIsNotified(prayerName, isNotified)

    /* MsApi1 */
    suspend fun getMsApi1() = msApi1Dao.getMsApi1()
    suspend fun updateMsApi1(msApi1: MsApi1) = msApi1Dao.updateMsApi1(
        msApi1.api1ID, msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year
    )

    /* MsFavAyah */
    suspend fun getMsFavAyah() = msFavAyahDao.getMsFavAyah()
    suspend fun getMsFavAyahBySurahID(surahID: Int) = msFavAyahDao.getMsFavAyahBySurahID(surahID)
    suspend fun insertFavAyah(msFavAyah: MsFavAyah) = msFavAyahDao.insertMsAyah(msFavAyah)
    suspend fun deleteFavAyah(msFavAyah: MsFavAyah) = msFavAyahDao.deleteMsFavAyah(msFavAyah)

    /* MsFavSurah */
    suspend fun getMsFavSurah() = msFavSurahDao.getMsFavSurah()
    suspend fun getMsFavSurahByID(ayahID: Int) = msFavSurahDao.getMsFavSurahBySurahID(ayahID)
    suspend fun insertFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.insertMsSurah(msFavSurah)
    suspend fun deleteFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.deleteMsFavSurah(msFavSurah)

    /* MsSetting */
    suspend fun getMsSetting() =  msSettingDao.getMsSetting()
    suspend fun updateIsUsingDBQuotes(isUsingDBQuotes: Boolean) = msSettingDao.updateIsUsingDBQuotes(isUsingDBQuotes)

    /*
     * Retrofit
     */
    suspend fun fetchCompass(msApi1: MsApi1) = remoteDataSourceAladhan.fetchCompassApi(msApi1)

    suspend fun fetchPrayerApi(msApi1: MsApi1) = remoteDataSourceAladhan.fetchPrayerApi(msApi1)

    suspend fun fetchReadSurahEn(surahID: Int) = remoteDataSourceApiAlquran.fetchReadSurahEn(surahID)

    suspend fun fetchAllSurah() = remoteDataSourceApiAlquran.fetchAllSurah()

    suspend fun fetchReadSurahAr(surahID: Int) = remoteDataSourceApiAlquran.fetchReadSurahAr(surahID)

    suspend fun syncNotifiedPrayer(msApi1: MsApi1): List<NotifiedPrayer> {

        /* val data =  remoteDataSourceAladhan.fetchPrayerApi(msApi1)

        data.body().let {
            val sdf = SimpleDateFormat("dd", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val timings = it?.data?.find { obj -> obj.date.gregorian?.day == currentDate.toString() }?.timings

            val map = mutableMapOf<String, String>()

            map[EnumConfig.fajr] = timings?.fajr.toString()
            map[EnumConfig.dhuhr] = timings?.dhuhr.toString()
            map[EnumConfig.asr] = timings?.asr.toString()
            map[EnumConfig.maghrib] = timings?.maghrib.toString()
            map[EnumConfig.isha] = timings?.isha.toString()
            map[EnumConfig.sunrise] = timings?.sunrise.toString()

            CoroutineScope(Dispatchers.IO).launch {
                map.forEach { p ->
                    notifiedPrayerDao.updatePrayerTime(p.key, p.value)
                }
            }
        } */

        val retVal = notifiedPrayerDao.getNotifiedPrayer()
        return retVal

        /* return object : NetworkBoundResource<List<NotifiedPrayer>, PrayerResponse>(){
            override fun loadFromDB(): LiveData<List<NotifiedPrayer>> = notifiedPrayerDao.getNotifiedPrayer()

            override fun shouldFetch(data: List<NotifiedPrayer>?): Boolean = true

            override fun createCall(): LiveData<Resource<PrayerResponse>> = remoteDataSourceAladhan.fetchPrayerApi(msApi1)

            override fun saveCallResult(data: PrayerResponse){
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

                CoroutineScope(Dispatchers.IO).launch {
                    map.forEach { p ->
                        notifiedPrayerDao.updatePrayerTime(p.key, p.value)
                    }
                }
            }
        }.asLiveData() */
    }

}