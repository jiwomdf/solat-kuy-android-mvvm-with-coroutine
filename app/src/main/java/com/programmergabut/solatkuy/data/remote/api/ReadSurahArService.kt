package com.programmergabut.solatkuy.data.remote.api

import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ReadSurahArService {

    //http://api.alquran.cloud/v1/surah/{surahID}

    @GET("surah/{surahID}")
    fun fetchReadSurahAr(@Path("surahID") surahID: Int): Call<ReadSurahArResponse>
}