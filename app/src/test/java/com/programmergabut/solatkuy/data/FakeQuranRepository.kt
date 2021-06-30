package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.programmergabut.solatkuy.base.BaseRepository
import com.programmergabut.solatkuy.data.local.dao.MsAyahDao
import com.programmergabut.solatkuy.data.local.dao.MsFavAyahDao
import com.programmergabut.solatkuy.data.local.dao.MsFavSurahDao
import com.programmergabut.solatkuy.data.local.dao.MsSurahDao
import com.programmergabut.solatkuy.data.local.localentity.MsAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.local.localentity.MsSurah
import com.programmergabut.solatkuy.data.remote.ApiResponse
import com.programmergabut.solatkuy.data.remote.api.AllSurahService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahArService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahEnService
import com.programmergabut.solatkuy.data.remote.json.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.ContextProviders
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.*
import retrofit2.await
import java.util.*

class FakeQuranRepository constructor(
    private val msFavAyahDao: MsFavAyahDao,
    private val msFavSurahDao: MsFavSurahDao,
    private val msSurahDao: MsSurahDao,
    private val msAyahDao: MsAyahDao,
    private val readSurahEnService: ReadSurahEnService,
    private val allSurahService: AllSurahService,
    private val readSurahArService: ReadSurahArService,
    private val contextProviders: ContextProviders,
):BaseRepository(), QuranRepository {

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
                response = execute(readSurahEnService.fetchReadSurahEn(surahID))
                response.status = "1"
            }
            catch (ex: Exception){
                response = ReadSurahEnResponse()
                response.status = "-1"
                response.message = ex.message.toString()
            }
            response
        }
    }

    override suspend fun fetchAllSurah(): Deferred<AllSurahResponse> {
        return CoroutineScope(Dispatchers.IO).async {
            lateinit var response: AllSurahResponse
            try {
                response = execute(allSurahService.fetchAllSurah())
                response.status = "1"
            }
            catch (ex: Exception){
                response = AllSurahResponse()
                response.status = "-1"
                response.message = ex.message.toString()
            }
            response
        }
    }

    override fun getAllSurah(): LiveData<Resource<List<MsSurah>>> {
        return object : NetworkBoundResource<List<MsSurah>, AllSurahResponse>(contextProviders) {
            override fun loadFromDB(): LiveData<List<MsSurah>> = msSurahDao.getSurahs()

            override fun shouldFetch(data: List<MsSurah>?): Boolean = data == null || data.isEmpty()

            override fun createCall(): LiveData<ApiResponse<AllSurahResponse>> {
                return liveData {
                    withContext(contextProviders.IO) {
                        lateinit var response: AllSurahResponse
                        try {
                            response = execute(allSurahService.fetchAllSurah())
                            emit(ApiResponse.success(response))
                        } catch (ex: Exception) {
                            response = AllSurahResponse()
                            response.message = ex.message.toString()
                            emit(ApiResponse.error(ex.message.toString(), response))
                        }
                    }
                }
            }

            override fun saveCallResult(data: AllSurahResponse) {
                val allSurah = data.data.map {
                    MsSurah(
                        englishName = it.englishName,
                        englishNameLowerCase = it.englishNameTranslation.lowercase(Locale.ROOT),
                        englishNameTranslation = it.englishNameTranslation,
                        name = it.name,
                        number = it.number,
                        numberOfAyahs = it.numberOfAyahs,
                        revelationType = it.revelationType
                    )
                }
                msSurahDao.insertSurahs(allSurah)
            }

        }.asLiveData()
    }

    override suspend fun fetchReadSurahAr(surahID: Int): Deferred<ReadSurahArResponse> {
        return CoroutineScope(Dispatchers.IO).async {
            lateinit var response : ReadSurahArResponse
            try {
                response = execute(readSurahArService.fetchReadSurahAr(surahID))
                response.status = "1"
            }
            catch (ex: Exception){
                response = ReadSurahArResponse()
                response.status = "-1"
                response.message = ex.message.toString()
            }
            response
        }
    }

    override fun getSelectedSurah(surahID: Int): LiveData<Resource<List<MsAyah>>> {
        TODO("Not yet implemented")
    }

}