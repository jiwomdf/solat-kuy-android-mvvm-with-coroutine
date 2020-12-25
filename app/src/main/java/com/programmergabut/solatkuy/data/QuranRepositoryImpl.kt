package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.programmergabut.solatkuy.data.local.dao.MsFavAyahDao
import com.programmergabut.solatkuy.data.local.dao.MsFavSurahDao
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquran
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceDecrement
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceIncrement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import java.lang.Exception
import javax.inject.Inject

class QuranRepositoryImpl @Inject constructor(
    private val remoteDataSourceApiAlquranImpl: RemoteDataSourceApiAlquran,
    private val msFavAyahDao: MsFavAyahDao,
    private val msFavSurahDao: MsFavSurahDao
): QuranRepository {

    /*
     *Room
     */
    /* MsFavAyah */
    override fun getListFavAyah(): LiveData<List<MsFavAyah>> = msFavAyahDao.getListFavAyah()
    override fun getListFavAyahBySurahID(surahID: Int): LiveData<List<MsFavAyah>> = msFavAyahDao.getListFavAyahBySurahID(surahID)
    override suspend fun insertFavAyah(msFavAyah: MsFavAyah) = msFavAyahDao.insertMsAyah(msFavAyah)
    override suspend fun deleteFavAyah(msFavAyah: MsFavAyah) = msFavAyahDao.deleteMsFavAyah(msFavAyah)

    /* MsFavSurah */
    override fun getListFavSurah(): LiveData<List<MsFavSurah>> = msFavSurahDao.getListFavSurah()
    override fun getFavSurahBySurahID(surahID: Int): LiveData<MsFavSurah> = msFavSurahDao.getFavSurahBySurahID(surahID)
    override suspend fun insertFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.insertMsSurah(msFavSurah)
    override suspend fun deleteFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.deleteMsFavSurah(msFavSurah)

    /*
     * Retrofit
     */
    override suspend fun fetchReadSurahEn(surahID: Int): Deferred<ReadSurahEnResponse> {
        return CoroutineScope(IO).async {
            lateinit var response: ReadSurahEnResponse
            try {
                response = remoteDataSourceApiAlquranImpl.fetchReadSurahEn(surahID)
                response.statusResponse = "1"
            }
            catch (ex: Exception){
                response = ReadSurahEnResponse()
                response.statusResponse = "-1"
                response.messageResponse = ex.message.toString()
            }
            response
        }
    }

    override suspend fun fetchAllSurah(): Deferred<AllSurahResponse> {
        return CoroutineScope(IO).async {
            lateinit var response: AllSurahResponse
            try {
                response = remoteDataSourceApiAlquranImpl.fetchAllSurah()
                response.statusResponse = "1"
            }
            catch (ex: Exception){
                response = AllSurahResponse()
                response.statusResponse = "-1"
                response.messageResponse = ex.message.toString()
            }
            response
        }
    }

    override suspend fun fetchReadSurahAr(surahID: Int): Deferred<ReadSurahArResponse> {
        return CoroutineScope(IO).async {
            lateinit var response : ReadSurahArResponse
            try {
                response = remoteDataSourceApiAlquranImpl.fetchReadSurahAr(surahID)
                response.statusResponse = "1"
            }
            catch (ex: Exception){
                response = ReadSurahArResponse()
                response.statusResponse = "-1"
                response.messageResponse = ex.message.toString()
            }
            response
        }
    }

}