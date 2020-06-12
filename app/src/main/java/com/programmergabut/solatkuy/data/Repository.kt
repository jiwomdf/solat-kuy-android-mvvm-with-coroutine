package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.local.LocalDataSource
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhan
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquran
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerApi
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArApi
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
                 private val remoteDataSourceAladhan: RemoteDataSourceAladhan,
                 private val remoteDataSourceApiAlquran: RemoteDataSourceApiAlquran,
                 private val localDataSource: LocalDataSource) {

    companion object{
        @Volatile
        private var instance: Repository? = null

        fun getInstance(contextProviders: ContextProviders,
                        remoteDataSourceAladhan: RemoteDataSourceAladhan,
                        remoteDataSourceApiAlquran: RemoteDataSourceApiAlquran,
                        localDataSource: LocalDataSource) =
            instance ?: synchronized(this){
                instance
                    ?: Repository(contextProviders, remoteDataSourceAladhan, remoteDataSourceApiAlquran, localDataSource)
            }
    }

    //Room
    fun getMsApi1() = localDataSource.getMsApi1()

    //fun getMsApi1() = localDataSource.getMsApi1()
    fun getMsSetting() = localDataSource.getMsSetting()
    fun getMsFavAyahByID(ayahID: Int) = localDataSource.getMsFavAyahByID(ayahID)

    //fun updateNotifiedPrayer(notifiedPrayer: NotifiedPrayer) = localDataSource.updateNotifiedPrayer(notifiedPrayer)
    //fun updatePrayerTime(prayerName: String, prayerTime: String) = localDataSource.updatePrayerTime(prayerName, prayerTime)
    //fun updateMsSetting(isHasOpen: Boolean) = localDataSource.updateMsSetting(isHasOpen)
    fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = localDataSource.updatePrayerIsNotified(prayerName, isNotified)
    fun updateMsApi1(msApi1: MsApi1) = localDataSource.updateMsApi1(msApi1)
    fun insertFavAyah(msFavAyah: MsFavAyah) = localDataSource.insertFavAyah(msFavAyah)

    //Retrofit
    fun fetchCompass(msApi1: MsApi1) = remoteDataSourceAladhan.fetchCompassApi(msApi1)

    //fun fetchAsmaAlHusna() = remoteDataSource.fetchAsmaAlHusnaApi()

    fun fetchPrayerApi(msApi1: MsApi1) = remoteDataSourceAladhan.fetchPrayerApi(msApi1)

    fun fetchReadSurahEn(surahID: Int) = remoteDataSourceApiAlquran.fetchReadSurahEn(surahID)

    fun fetchAllSurah() = remoteDataSourceApiAlquran.fetchAllSurah()

    fun fetchReadSurahAr(surahID: Int): MutableLiveData<Resource<ReadSurahArApi>> = remoteDataSourceApiAlquran.fetchReadSurahAr(surahID)

    /* return object : NetworkBoundResource<List<MsFavAyah>, ReadSurahArApi>(contextProviders){
        override fun loadFromDB(): LiveData<List<MsFavAyah>> = localDataSource.getMsFavAyah()

        override fun shouldFetch(data: List<MsFavAyah>?): Boolean = true

        override fun createCall(): LiveData<Resource<ReadSurahArApi>> = remoteDataSourceApiAlquran.fetchReadSurahAr(surahID)

        override fun saveCallResult(data: ReadSurahArApi) {

            val msFavAyah = loadFromDB().value

            data.data.ayahs.forEach{remoteAyah ->

                msFavAyah?.forEach {
                    if(it.ayahID == remoteAyah.numberInSurah){

                    }
                }

            }
        }

    }.asLiveData() */

    /* val result = MutableLiveData<Resource<ReadSurahArApi>>()

    GlobalScope.launch(contextProviders.IO){

        val remoteData = remoteDataSourceApiAlquran.fetchReadSurahAr(surahID)

        val selReadSurah = remoteData.value?.data?.data

        val localFavData = localDataSource.getMsFavAyahByID()


        if(localFavData.value.isNullOrEmpty())
            result.postValue(remoteData.value)
        else{
            val selSurahFav = localFavData.value!!.filter { x -> x.favAyahID == surahID.toInt() }

            selReadSurah?.ayahs?.forEach { remote ->

                selSurahFav.forEach {localFavSurah ->

                    if(remote.numberInSurah == localFavSurah.favAyahID)
                        remote.isFav = true

                }
            }
        }

        result.postValue(remoteData.value)
    }

        return result
    */


    fun isFavoriteAyah(ayahID: Int, surahID: Int): LiveData<Boolean> {
        val isFavorite = MediatorLiveData<Boolean>()
        val listfavAyah = localDataSource.isFavAyah(ayahID, surahID)

        listfavAyah.value

        isFavorite.addSource(listfavAyah) { data ->
            if(data == null)
                isFavorite.value = false
            else
                isFavorite.value = ayahID == data.ayahID
        }

        return isFavorite
    }

    fun syncNotifiedPrayer(msApi1: MsApi1): LiveData<Resource<List<NotifiedPrayer>>> {

        return object : NetworkBoundResource<List<NotifiedPrayer>, PrayerApi>(contextProviders){
            override fun loadFromDB(): LiveData<List<NotifiedPrayer>> = localDataSource.getNotifiedPrayer()

            override fun shouldFetch(data: List<NotifiedPrayer>?): Boolean = true

            override fun createCall(): LiveData<Resource<PrayerApi>> = remoteDataSourceAladhan.fetchPrayerApi(msApi1)

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