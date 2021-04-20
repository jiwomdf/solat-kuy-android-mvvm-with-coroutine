package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.data.remote.json.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonAr.Ayah
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.Deferred

interface QuranRepository {
    fun observeListFavAyah(): LiveData<List<MsFavAyah>>
    suspend fun getListFavAyahBySurahID(surahID: Int): List<MsFavAyah>?
    suspend fun insertFavAyah(msFavAyah: MsFavAyah)
    suspend fun deleteFavAyah(msFavAyah: MsFavAyah)
    fun observeListFavSurah(): LiveData<List<MsFavSurah>>
    fun observeFavSurahBySurahID(surahID: Int): LiveData<MsFavSurah?>
    suspend fun insertFavSurah(msFavSurah: MsFavSurah)
    suspend fun deleteFavSurah(msFavSurah: MsFavSurah)
    suspend fun fetchReadSurahEn(surahID: Int): Deferred<ReadSurahEnResponse>
    suspend fun fetchAllSurah(): Deferred<AllSurahResponse>
    fun getAllSurah(): LiveData<Resource<List<MsSurah>>>
    suspend fun fetchReadSurahAr(surahID: Int): Deferred<ReadSurahArResponse>
    fun getReadSurahAr(surahID: Int): LiveData<Resource<List<MsAyah>>>
}