package com.programmergabut.solatkuy.data.remote

import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import retrofit2.Call

interface RemoteDataSourceApiAlquran {
    fun fetchReadSurahEn(surahID: Int): ReadSurahEnResponse
    fun fetchAllSurah(): AllSurahResponse
    fun fetchReadSurahAr(surahID: Int): ReadSurahArResponse
}