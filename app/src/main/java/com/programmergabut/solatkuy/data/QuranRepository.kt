package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource

interface QuranRepository {
    fun getListFavAyah(): LiveData<Resource<List<MsFavAyah>>>
    fun getListFavAyahBySurahID(surahID: Int): LiveData<Resource<List<MsFavAyah>>>
    suspend fun insertFavAyah(msFavAyah: MsFavAyah)
    suspend fun deleteFavAyah(msFavAyah: MsFavAyah)
    fun getListFavSurah(): LiveData<Resource<List<MsFavSurah>>>
    fun getFavSurahBySurahID(surahID: Int): LiveData<Resource<MsFavSurah>>
    suspend fun insertFavSurah(msFavSurah: MsFavSurah)
    suspend fun deleteFavSurah(msFavSurah: MsFavSurah)
    suspend fun fetchReadSurahEn(surahID: Int): ReadSurahEnResponse
    suspend fun fetchAllSurah(): AllSurahResponse
    suspend fun fetchReadSurahAr(surahID: Int): ReadSurahArResponse
}