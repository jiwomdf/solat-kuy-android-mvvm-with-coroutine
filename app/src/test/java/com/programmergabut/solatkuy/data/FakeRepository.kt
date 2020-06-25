package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.local.LocalDataSource
import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhan
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquran
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

class FakeRepository(private val remoteDataSourceAladhan: RemoteDataSourceAladhan,
                     private val remoteDataSourceApiAlquran: RemoteDataSourceApiAlquran,
                     private val localDataSource: LocalDataSource
) {

    /* Room */
    /* NotifiedPrayer */
    suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = localDataSource.updatePrayerIsNotified(prayerName, isNotified)

    /* MsApi1 */
    fun getMsApi1(): LiveData<Resource<MsApi1>> {
        val data = MediatorLiveData<Resource<MsApi1>>()
        val msApi1 = localDataSource.getMsApi1()

        data.value = Resource.loading(null)

        data.addSource(msApi1) {
            data.value = Resource.success(it)
        }

        return data
    }
    suspend fun updateMsApi1(msApi1: MsApi1) = localDataSource.updateMsApi1(msApi1)

    /* MsFavAyah */
    fun getMsFavAyah() : LiveData<Resource<List<MsFavAyah>>> {
        val data = MediatorLiveData<Resource<List<MsFavAyah>>>()
        val listfavAyah = localDataSource.getMsFavAyah()

        data.value = Resource.loading(null)

        data.addSource(listfavAyah) {
            data.value = Resource.success(it)
        }

        return data
    }
    fun getMsFavAyahBySurahID(surahID: Int): LiveData<Resource<List<MsFavAyah>>> {
        val data = MediatorLiveData<Resource<List<MsFavAyah>>>()
        val listfavAyah = localDataSource.getMsFavAyahBySurahID(surahID)

        data.value = Resource.loading(null)

        data.addSource(listfavAyah) {
            data.value = Resource.success(it)
        }

        return data
    }
    suspend fun insertFavAyah(msFavAyah: MsFavAyah) = localDataSource.insertFavAyah(msFavAyah)
    suspend fun deleteFavAyah(msFavAyah: MsFavAyah) = localDataSource.deleteFavAyah(msFavAyah)

    /* MsFavSurah */
    fun getMsFavSurah(): LiveData<Resource<List<MsFavSurah>>> {
        val data = MediatorLiveData<Resource<List<MsFavSurah>>>()
        val listfavSurah = localDataSource.getMsFavSurah()

        data.value = Resource.loading(null)

        data.addSource(listfavSurah) {
            data.value = Resource.success(it)
        }

        return data
    }
    fun getMsFavSurahByID(ayahID: Int): LiveData<Resource<MsFavSurah>> {
        val data = MediatorLiveData<Resource<MsFavSurah>>()
        val listfavSurah = localDataSource.getMsFavSurahByID(ayahID)

        data.value = Resource.loading(null)

        data.addSource(listfavSurah) {
            data.value = Resource.success(it)
        }

        return data
    }
    suspend fun insertFavSurah(msFavSurah: MsFavSurah) = localDataSource.insertFavSurah(msFavSurah)
    suspend fun deleteFavSurah(msFavSurah: MsFavSurah) = localDataSource.deleteFavSurah(msFavSurah)

    /* MsSetting */
    fun getMsSetting(): LiveData<Resource<MsSetting>> {
        val data = MediatorLiveData<Resource<MsSetting>>()
        val msSetting = localDataSource.getMsSetting()

        data.value = Resource.loading(null)

        data.addSource(msSetting) {
            data.value = Resource.success(it)
        }

        return data
    }
    suspend fun updateIsUsingDBQuotes(isUsingDBQuotes: Boolean) = localDataSource.updateIsUsingDBQuotes(isUsingDBQuotes)

    /*
     * Retrofit
     */
    fun fetchCompass(msApi1: MsApi1) = remoteDataSourceAladhan.fetchCompassApi(msApi1)

    fun fetchPrayerApi(msApi1: MsApi1) = remoteDataSourceAladhan.fetchPrayerApi(msApi1)

    fun fetchReadSurahEn(surahID: Int) = remoteDataSourceApiAlquran.fetchReadSurahEn(surahID)

    fun fetchAllSurah() = remoteDataSourceApiAlquran.fetchAllSurah()

    fun fetchReadSurahAr(surahID: Int): MutableLiveData<Resource<ReadSurahArResponse>> = remoteDataSourceApiAlquran.fetchReadSurahAr(surahID)

    fun syncNotifiedPrayer(msApi1: MsApi1): LiveData<Resource<List<NotifiedPrayer>>> {

        return object : NetworkBoundResource<List<NotifiedPrayer>, PrayerResponse>(){
            override fun loadFromDB(): LiveData<List<NotifiedPrayer>> = localDataSource.getNotifiedPrayer()

            override fun shouldFetch(data: List<NotifiedPrayer>?): Boolean = true

            override fun createCall(): LiveData<Resource<PrayerResponse>> = remoteDataSourceAladhan.fetchPrayerApi(msApi1)

            override fun saveCallResult(data: PrayerResponse) = localDataSource.updateListPrayerTime(data)
        }.asLiveData()
    }

}