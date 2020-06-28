package com.programmergabut.solatkuy.data.remote

import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.remote.api.AllSurahService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahArService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahEnService
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.EspressoIdlingResource
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.*
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

class RemoteDataSourceApiAlquran constructor(){

    private val strApi = "https://api.alquran.cloud/v1/"

    companion object{
        @Volatile
        private var instance: RemoteDataSourceApiAlquran? = null

        fun getInstance() = instance
            ?: synchronized(this){
                instance
                    ?: RemoteDataSourceApiAlquran()
            }
    }


    suspend fun fetchReadSurahEn(surahID: Int): Response<ReadSurahEnResponse> {

        return RetrofitBuilder
                .build<ReadSurahEnService>(strApi)
                .fetchReadSurahEn(surahID)
    }


    suspend fun fetchAllSurah(): Response<AllSurahResponse> {

        return RetrofitBuilder
                .build<AllSurahService>(strApi)
                .fetchAllSurah()
    }

    suspend fun fetchReadSurahAr(surahID: Int): ReadSurahArResponse {
        //EspressoIdlingResource.increment()

        val listAyah: MutableList<Ayah>

        val retValAr = withContext(Dispatchers.Default) {
            RetrofitBuilder.build<ReadSurahArService>(strApi).fetchReadSurahAr(surahID)
        }

        val retValEn = withContext(Dispatchers.Default) {
            RetrofitBuilder.build<ReadSurahEnService>(strApi).fetchReadSurahEn(surahID)
        }

        listAyah = retValAr.data.ayahs.map { x -> Ayah(
            x.hizbQuarter,
            x.juz,
            x.manzil,
            x.number,
            x.numberInSurah,
            x.page,
            x.ruku,
            x.text,
            "",
            false)} as MutableList<Ayah>

        retValEn.body()?.data?.ayahs?.forEachIndexed { index, x ->
            listAyah[index].textEn = x.text
        }


        retValAr.data.ayahs = listAyah

        return retValAr

        //EspressoIdlingResource.decrement()
    }

}