package com.programmergabut.solatkuy.data.remote

import android.util.Log
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

class RemoteDataSourceApiAlquranImpl @Inject constructor(
    private val readSurahEnService: ReadSurahEnService,
    private val allSurahService: AllSurahService,
    private val readSurahArService: ReadSurahArService
): RemoteDataSourceApiAlquran{

    override suspend fun fetchReadSurahEn(surahID: Int): Response<ReadSurahEnResponse> {
        return readSurahEnService.fetchReadSurahEn(surahID)
    }


    override suspend fun fetchAllSurah(): Response<AllSurahResponse> {

        return allSurahService.fetchAllSurah()
    }

    override suspend fun fetchReadSurahAr(surahID: Int): ReadSurahArResponse {
        //EspressoIdlingResource.increment()

        val listAyah: MutableList<Ayah>

        val retValAr = readSurahArService.fetchReadSurahAr(surahID)
        Log.d("fetchReadSurahAr", "retValAr")

        val retValEn = readSurahEnService.fetchReadSurahEn(surahID)
        Log.d("fetchReadSurahAr", "retValEn")

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
        Log.d("fetchReadSurahAr", "listAyah")

        return retValAr

        //EspressoIdlingResource.decrement()
    }

}