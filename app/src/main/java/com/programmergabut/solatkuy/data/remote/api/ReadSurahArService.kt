package com.programmergabut.solatkuy.data.remote.api

import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArApi
import retrofit2.http.GET
import retrofit2.http.Path

interface ReadSurahArService {


    //http://api.alquran.cloud/v1/surah/{surahID}

    @GET("surah/{surahID}")
    suspend fun fetchReadSurahAr(@Path("surahID") surahID: Int): ReadSurahArApi


}