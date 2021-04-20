package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import com.programmergabut.solatkuy.data.local.dao.MsFavAyahDao
import com.programmergabut.solatkuy.data.local.dao.MsFavSurahDao
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.json.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonEn.ReadSurahEnResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class FakeQuranRepository constructor(
    private val remoteDataSourceApiAlquran: RemoteDataSourceApiAlquran,
    private val msFavAyahDao: MsFavAyahDao,
    private val msFavSurahDao: MsFavSurahDao
): QuranRepository {

    /* MsFavAyah */
    override fun observeListFavAyah(): LiveData<List<MsFavAyah>> = msFavAyahDao.observeListFavAyah()
    override suspend fun getListFavAyahBySurahID(surahID: Int): List<MsFavAyah>? = msFavAyahDao.getListFavAyahBySurahID(surahID)
    override suspend fun insertFavAyah(msFavAyah: MsFavAyah) = msFavAyahDao.insertMsAyah(msFavAyah)
    override suspend fun deleteFavAyah(msFavAyah: MsFavAyah) = msFavAyahDao.deleteMsFavAyah(msFavAyah)

    /* MsFavSurah */
    override fun observeListFavSurah() = msFavSurahDao.observeListFavSurah()
    override fun observeFavSurahBySurahID(surahID: Int) = msFavSurahDao.observeFavSurahBySurahID(surahID)
    override suspend fun insertFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.insertMsSurah(msFavSurah)
    override suspend fun deleteFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.deleteMsFavSurah(msFavSurah)

    /* Remote */
    override suspend fun fetchReadSurahEn(surahID: Int): Deferred<ReadSurahEnResponse> {
        return CoroutineScope(Dispatchers.IO).async {
            lateinit var response: ReadSurahEnResponse
            try {
                response = remoteDataSourceApiAlquran.fetchReadSurahEn(surahID)
                response.statusResponse = "1"
            }
            catch (ex: Exception){
                response = ReadSurahEnResponse()
                response.statusResponse = "-1"
                response.message = ex.message.toString()
            }
            response
        }
    }

    override suspend fun fetchAllSurah(): Deferred<AllSurahResponse> {
        return CoroutineScope(Dispatchers.IO).async {
            lateinit var response: AllSurahResponse
            try {
                response = remoteDataSourceApiAlquran.fetchAllSurah()
                response.statusResponse = "1"
            }
            catch (ex: Exception){
                response = AllSurahResponse()
                response.statusResponse = "-1"
                response.message = ex.message.toString()
            }
            response
        }
    }

    override suspend fun fetchReadSurahAr(surahID: Int): Deferred<ReadSurahArResponse> {
        return CoroutineScope(Dispatchers.IO).async {
            lateinit var response : ReadSurahArResponse
            try {
                response = remoteDataSourceApiAlquran.fetchReadSurahAr(surahID)
                response.statusResponse = "1"
            }
            catch (ex: Exception){
                response = ReadSurahArResponse()
                response.statusResponse = "-1"
                response.message = ex.message.toString()
            }
            response
        }
    }

}