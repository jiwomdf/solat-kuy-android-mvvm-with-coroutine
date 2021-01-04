package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.DummyRetValueAndroidTest
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class FakeQuranRepositoryAndroidTest: QuranRepository {

    private var listMsFavAyah = DummyRetValueAndroidTest.getListMsFavAyah()
    private val observableMsFavAyahs = MutableLiveData<List<MsFavAyah>>()
    private fun refreshMsFavAyah(){
        observableMsFavAyahs.postValue(listMsFavAyah)
    }

    private var listMsFavSurah = DummyRetValueAndroidTest.getListMsFavSurah()
    private val observableMsFavSurahs = MutableLiveData<List<MsFavSurah>>()
    private val observableMsFavSurah = MutableLiveData<MsFavSurah>()
    private fun refreshMsFavSurah(){
        observableMsFavSurahs.postValue(listMsFavSurah)
    }


    /*
     *Room
     */
    /* MsFavAyah */
    override fun getListFavAyah(): LiveData<List<MsFavAyah>> {
        return observableMsFavAyahs
    }
    override fun getListFavAyahBySurahID(surahID: Int): LiveData<List<MsFavAyah>> {
        return observableMsFavAyahs
    }
    override suspend fun insertFavAyah(msFavAyah: MsFavAyah){
        listMsFavAyah.add(msFavAyah)
        refreshMsFavAyah()
    }
    override suspend fun deleteFavAyah(msFavAyah: MsFavAyah){
        listMsFavAyah.remove(msFavAyah)
        refreshMsFavAyah()
    }

    /* MsFavSurah */
    override fun getListFavSurah(): LiveData<List<MsFavSurah>> {
        return observableMsFavSurahs
    }
    override fun getFavSurahBySurahID(surahID: Int): LiveData<MsFavSurah> {
        return observableMsFavSurah
    }
    override suspend fun insertFavSurah(msFavSurah: MsFavSurah){
        listMsFavSurah.add(msFavSurah)
        refreshMsFavSurah()
    }
    override suspend fun deleteFavSurah(msFavSurah: MsFavSurah){
        listMsFavSurah.add(msFavSurah)
        refreshMsFavSurah()
    }

    /*
     * Retrofit
     */
    override suspend fun fetchReadSurahEn(surahID: Int): Deferred<ReadSurahEnResponse> {
        return CoroutineScope(IO).async {
            DummyRetValueAndroidTest.surahEnID_1<FakeQuranRepositoryAndroidTest>()
        }
    }
    override suspend fun fetchAllSurah(): Deferred<AllSurahResponse> {
        return CoroutineScope(IO).async {
            DummyRetValueAndroidTest.fetchAllSurah<FakeQuranRepositoryAndroidTest>()
        }
    }
    override suspend fun fetchReadSurahAr(surahID: Int): Deferred<ReadSurahArResponse> {
        return CoroutineScope(IO).async {
            DummyRetValueAndroidTest.surahArID_1<FakeQuranRepositoryAndroidTest>()
        }
    }

}