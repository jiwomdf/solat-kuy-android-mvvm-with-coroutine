package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhanImpl
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquranImpl
import com.programmergabut.solatkuy.util.Resource
import javax.inject.Inject

class QuranRepository @Inject constructor(
    private val remoteDataSourceApiAlquranImpl: RemoteDataSourceApiAlquranImpl,
    private val msFavAyahDao: MsFavAyahDao,
    private val msFavSurahDao: MsFavSurahDao
) {

    /*
     *Room
     */
    /* MsFavAyah */
    fun getListFavAyah(): LiveData<Resource<List<MsFavAyah>>> {
        val data = MediatorLiveData<Resource<List<MsFavAyah>>>()
        val listfavAyah = msFavAyahDao.getListFavAyah()

        data.value = Resource.loading(null)

        data.addSource(listfavAyah) {
            data.value = Resource.success(it)
        }

        return data
    }
    fun getListFavAyahBySurahID(surahID: Int): LiveData<Resource<List<MsFavAyah>>> {
        val data = MediatorLiveData<Resource<List<MsFavAyah>>>()
        val listfavAyah = msFavAyahDao.getListFavAyahBySurahID(surahID)

        data.value = Resource.loading(null)

        data.addSource(listfavAyah) {
            data.value = Resource.success(it)
        }

        return data
    }
    suspend fun insertFavAyah(msFavAyah: MsFavAyah) = msFavAyahDao.insertMsAyah(msFavAyah)
    suspend fun deleteFavAyah(msFavAyah: MsFavAyah) = msFavAyahDao.deleteMsFavAyah(msFavAyah)

    /* MsFavSurah */
    fun getListFavSurah(): LiveData<Resource<List<MsFavSurah>>> {
        val data = MediatorLiveData<Resource<List<MsFavSurah>>>()
        val listfavSurah = msFavSurahDao.getListFavSurah()

        data.value = Resource.loading(null)

        data.addSource(listfavSurah) {
            data.value = Resource.success(it)
        }

        return data
    }
    fun getFavSurahBySurahID(surahID: Int): LiveData<Resource<MsFavSurah>> {
        val data = MediatorLiveData<Resource<MsFavSurah>>()
        val listfavSurah = msFavSurahDao.getFavSurahBySurahID(surahID)

        data.value = Resource.loading(null)

        data.addSource(listfavSurah) {
            data.value = Resource.success(it)
        }

        return data
    }
    suspend fun insertFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.insertMsSurah(msFavSurah)
    suspend fun deleteFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.deleteMsFavSurah(msFavSurah)

    /*
     * Retrofit
     */
    suspend fun fetchReadSurahEn(surahID: Int) = remoteDataSourceApiAlquranImpl.fetchReadSurahEn(surahID)
    suspend fun fetchAllSurah() = remoteDataSourceApiAlquranImpl.fetchAllSurah()
    suspend fun fetchReadSurahAr(surahID: Int) = remoteDataSourceApiAlquranImpl.fetchReadSurahAr(surahID)

}