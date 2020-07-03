package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhanImpl
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquranImpl
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import java.text.SimpleDateFormat
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

class FakeRepository constructor(
    private val remoteDataSourceAladhanImpl: RemoteDataSourceAladhanImpl,
    private val remoteDataSourceApiAlquranImpl: RemoteDataSourceApiAlquranImpl,
    private val notifiedPrayerDao: NotifiedPrayerDao,
    private val msApi1Dao: MsApi1Dao,
    private val msSettingDao: MsSettingDao,
    private val msFavAyahDao: MsFavAyahDao,
    private val msFavSurahDao: MsFavSurahDao
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

    /* MsFavAyah */
    fun getListFavAyah(): LiveData<Resource<List<MsFavAyah>>> {
        val data = MediatorLiveData<Resource<List<MsFavAyah>>>()
        val listfavAyah = msFavAyahDao.getListFavAyah()

        data.value = Resource.loading(null)

        data.addSource(listfavAyah) {
            data.value = Resource.success(it)
        }

        return data
    }
    fun getListFavAyahBySurahID(surahID: Int): LiveData<Resource<List<MsFavAyah>>> {
        val data = MediatorLiveData<Resource<List<MsFavAyah>>>()
        val listfavAyah = msFavAyahDao.getListFavAyahBySurahID(surahID)

        data.value = Resource.loading(null)

        data.addSource(listfavAyah) {
            data.value = Resource.success(it)
        }

        return data
    }
    suspend fun insertFavAyah(msFavAyah: MsFavAyah) = msFavAyahDao.insertMsAyah(msFavAyah)
    suspend fun deleteFavAyah(msFavAyah: MsFavAyah) = msFavAyahDao.deleteMsFavAyah(msFavAyah)

    /* MsFavSurah */
    fun getListFavSurah(): LiveData<Resource<List<MsFavSurah>>> {
        val data = MediatorLiveData<Resource<List<MsFavSurah>>>()
        val listfavSurah = msFavSurahDao.getListFavSurah()

        data.value = Resource.loading(null)

        data.addSource(listfavSurah) {
            data.value = Resource.success(it)
        }

        return data
    }
    fun getFavSurahBySurahID(ayahID: Int): LiveData<Resource<MsFavSurah>> {
        val data = MediatorLiveData<Resource<MsFavSurah>>()
        val listfavSurah = msFavSurahDao.getFavSurahBySurahID(ayahID)

        data.value = Resource.loading(null)

        data.addSource(listfavSurah) {
            data.value = Resource.success(it)
        }

        return data
    }
    suspend fun insertFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.insertMsSurah(msFavSurah)
    suspend fun deleteFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.deleteMsFavSurah(msFavSurah)

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

    /*
     * Retrofit
     */
    suspend fun fetchCompass(msApi1: MsApi1) = remoteDataSourceAladhanImpl.fetchCompassApi(msApi1)

    suspend fun fetchPrayerApi(msApi1: MsApi1) = remoteDataSourceAladhanImpl.fetchPrayerApi(msApi1)

    suspend fun fetchReadSurahEn(surahID: Int) = remoteDataSourceApiAlquranImpl.fetchReadSurahEn(surahID)

    suspend fun fetchAllSurah() = remoteDataSourceApiAlquranImpl.fetchAllSurah()

    suspend fun fetchReadSurahAr(surahID: Int) = remoteDataSourceApiAlquranImpl.fetchReadSurahAr(surahID)

    suspend fun syncNotifiedPrayer(msApi1: MsApi1): List<NotifiedPrayer> {

        val data = remoteDataSourceAladhanImpl.fetchPrayerApi(msApi1)
        //.d("syncNotifiedPrayer", "fetch")

        data.let {
            val sdf = SimpleDateFormat("dd", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val timings =
                it.data.find { obj -> obj.date.gregorian?.day == currentDate.toString() }?.timings

            val map = mutableMapOf<String, String>()

            map[EnumConfig.fajr] = timings?.fajr.toString()
            map[EnumConfig.dhuhr] = timings?.dhuhr.toString()
            map[EnumConfig.asr] = timings?.asr.toString()
            map[EnumConfig.maghrib] = timings?.maghrib.toString()
            map[EnumConfig.isha] = timings?.isha.toString()
            map[EnumConfig.sunrise] = timings?.sunrise.toString()


            map.forEach { p ->
                notifiedPrayerDao.updatePrayerTime(p.key, p.value)
                //.d("syncNotifiedPrayer", "updated")
            }
        }

        val ret = notifiedPrayerDao.getListNotifiedPrayerSync()

        //.d("syncNotifiedPrayer", "Ret")

        return ret
    }
}