package com.programmergabut.solatkuy.data.remote.api

import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahApi
import retrofit2.http.GET

interface AllSurahService {

    //http://api.alquran.cloud/v1/juz/30/en.asad

    @GET("surah")
    suspend fun fetchAllSurah(): AllSurahApi

}