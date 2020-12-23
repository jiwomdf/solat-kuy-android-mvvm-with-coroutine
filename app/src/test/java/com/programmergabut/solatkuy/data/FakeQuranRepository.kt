package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquranImpl
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.lang.Exception

class FakeQuranRepository constructor(
    private val remoteDataSourceApiAlquranImpl: RemoteDataSourceApiAlquranImpl,
    private val msFavAyahDao: MsFavAyahDao,
    private val msFavSurahDao: MsFavSurahDao
): QuranRepository {

    /*
     *Room
     */
    /* MsFavAyah */
    override fun getListFavAyah(): LiveData<List<MsFavAyah>> {
        return msFavAyahDao.getListFavAyah()
    }
    override fun getListFavAyahBySurahID(surahID: Int): LiveData<List<MsFavAyah>> {
        return msFavAyahDao.getListFavAyahBySurahID(surahID)
    }
    override suspend fun insertFavAyah(msFavAyah: MsFavAyah) = msFavAyahDao.insertMsAyah(msFavAyah)
    override suspend fun deleteFavAyah(msFavAyah: MsFavAyah) = msFavAyahDao.deleteMsFavAyah(msFavAyah)

    /* MsFavSurah */
    override fun getListFavSurah(): LiveData<Resource<List<MsFavSurah>>> {
        val data = MediatorLiveData<Resource<List<MsFavSurah>>>()
        val listfavSurah = msFavSurahDao.getListFavSurah()

        data.value = Resource.loading(null)

        data.addSource(listfavSurah) {
            data.value = Resource.success(it)
        }

        return data
    }
    override fun getFavSurahBySurahID(surahID: Int): LiveData<Resource<MsFavSurah>> {
        val data = MediatorLiveData<Resource<MsFavSurah>>()
        val listfavSurah = msFavSurahDao.getFavSurahBySurahID(surahID)

        data.value = Resource.loading(null)

        data.addSource(listfavSurah) {
            data.value = Resource.success(it)
        }

        return data
    }
    override suspend fun insertFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.insertMsSurah(msFavSurah)
    override suspend fun deleteFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.deleteMsFavSurah(msFavSurah)

    /*
     * Retrofit
     */
    override suspend fun fetchReadSurahEn(surahID: Int): Deferred<ReadSurahEnResponse> {
        return CoroutineScope(Dispatchers.IO).async {
            lateinit var response: ReadSurahEnResponse
            try {
                response = remoteDataSourceApiAlquranImpl.fetchReadSurahEn(surahID)
                response.statusResponse = "1"
            }
            catch (ex: Exception){
                response.statusResponse = "-1"
                response.messageResponse = ex.message.toString()
            }
            response
        }
    }

    override suspend fun fetchAllSurah(): Deferred<AllSurahResponse> {
        return CoroutineScope(Dispatchers.IO).async {
            lateinit var response: AllSurahResponse
            try {
                response = remoteDataSourceApiAlquranImpl.fetchAllSurah()
                response.statusResponse = "1"
            }
            catch (ex: Exception){
                response.statusResponse = "-1"
                response.messageResponse = ex.message.toString()
            }
            response
        }
    }

    override suspend fun fetchReadSurahAr(surahID: Int): Deferred<ReadSurahArResponse> {
        return CoroutineScope(Dispatchers.IO).async {
            lateinit var response : ReadSurahArResponse
            try {
                response = remoteDataSourceApiAlquranImpl.fetchReadSurahAr(surahID)
                response.statusResponse = "1"
            }
            catch (ex: Exception){
                response.statusResponse = "-1"
                response.messageResponse = ex.message.toString()
            }
            response
        }
    }

}