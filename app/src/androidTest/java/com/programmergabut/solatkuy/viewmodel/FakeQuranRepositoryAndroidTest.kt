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
import javax.inject.Inject

class FakeQuranRepositoryAndroidTest: QuranRepository {

    private var listMsFavAyah = DummyRetValue.getListMsFavAyah()
    private val observableMsFavAyahs = MutableLiveData<Resource<List<MsFavAyah>>>()
    private fun refreshMsFavAyah(){
        observableMsFavAyahs.postValue(Resource.success(listMsFavAyah))
    }

    private var listMsFavSurah = DummyRetValue.getListMsFavSurah()
    private val observableMsFavSurahs = MutableLiveData<Resource<List<MsFavSurah>>>()
    private val observableMsFavSurah = MutableLiveData<Resource<MsFavSurah>>()
    private fun refreshMsFavSurah(){
        observableMsFavSurahs.postValue(Resource.success(listMsFavSurah))
    }


    /*
     *Room
     */
    /* MsFavAyah */
    override fun getListFavAyah(): LiveData<Resource<List<MsFavAyah>>> {
        return observableMsFavAyahs
    }
    override fun getListFavAyahBySurahID(surahID: Int): LiveData<Resource<List<MsFavAyah>>> {
        return observableMsFavAyahs
    }
    override suspend fun insertFavAyah(msFavAyah: MsFavAyah){}
    override suspend fun deleteFavAyah(msFavAyah: MsFavAyah){}

    /* MsFavSurah */
    override fun getListFavSurah(): LiveData<Resource<List<MsFavSurah>>> {
        return observableMsFavSurahs
    }
    override fun getFavSurahBySurahID(surahID: Int): LiveData<Resource<MsFavSurah>> {
        return observableMsFavSurah
    }
    override suspend fun insertFavSurah(msFavSurah: MsFavSurah){}
    override suspend fun deleteFavSurah(msFavSurah: MsFavSurah){}

    /*
     * Retrofit
     */
    override suspend fun fetchReadSurahEn(surahID: Int): ReadSurahEnResponse {
        return DummyRetValue.surahEnID_1()
    }
    override suspend fun fetchAllSurah(): AllSurahResponse {
        return DummyRetValue.fetchAllSurah()
    }
    override suspend fun fetchReadSurahAr(surahID: Int): ReadSurahArResponse {
        return DummyRetValue.surahArID_1()
    }

}