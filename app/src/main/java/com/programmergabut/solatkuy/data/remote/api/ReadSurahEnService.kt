package com.programmergabut.solatkuy.data.remote.api

import com.programmergabut.solatkuy.data.remote.json.readsurahJsonEn.ReadSurahEnResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/*
 * Created by Katili Jiwo Adi Wiyono on 20/05/20.
 */

interface ReadSurahEnService {

    //http://api.alquran.cloud/v1/surah/110/en.asad

    @GET("surah/{nInSurah}/en.asad")
    fun fetchReadSurahEn(@Path("nInSurah") nInSurah: Int): Call<ReadSurahEnResponse>
}