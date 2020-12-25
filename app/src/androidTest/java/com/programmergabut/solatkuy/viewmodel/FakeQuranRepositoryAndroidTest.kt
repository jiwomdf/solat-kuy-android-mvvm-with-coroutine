package com.programmergabut.solatkuy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhanImpl
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquranImpl
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.ui.DummyRetValue
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import javax.inject.Inject

class FakeQuranRepositoryAndroidTest: QuranRepository {

    private var listMsFavAyah = DummyRetValue.getListMsFavAyah()
    private val observableMsFavAyahs = MutableLiveData<List<MsFavAyah>>()
    private fun refreshMsFavAyah(){
        observableMsFavAyahs.postValue(listMsFavAyah)
    }

    private var listMsFavSurah = DummyRetValue.getListMsFavSurah()
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
            DummyRetValue.surahEnID_1()
        }
    }
    override suspend fun fetchAllSurah(): Deferred<AllSurahResponse> {
        return CoroutineScope(IO).async {
            DummyRetValue.fetchAllSurah()
        }
    }
    override suspend fun fetchReadSurahAr(surahID: Int): Deferred<ReadSurahArResponse> {
        return CoroutineScope(IO).async {
            DummyRetValue.surahArID_1()
        }
    }

}