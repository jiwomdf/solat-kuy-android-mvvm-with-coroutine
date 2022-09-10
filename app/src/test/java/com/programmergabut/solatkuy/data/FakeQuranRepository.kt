package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.programmergabut.solatkuy.base.BaseRepository
import com.programmergabut.solatkuy.data.local.dao.MsAyahDao
import com.programmergabut.solatkuy.data.local.dao.MsFavSurahDao
import com.programmergabut.solatkuy.data.local.dao.MsSurahDao
import com.programmergabut.solatkuy.data.local.localentity.MsAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.local.localentity.MsSurah
import com.programmergabut.solatkuy.data.remote.ApiResponse
import com.programmergabut.solatkuy.data.remote.api.AllSurahService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahArService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahEnService
import com.programmergabut.solatkuy.data.remote.json.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.data.repository.NetworkBoundResource
import com.programmergabut.solatkuy.data.repository.QuranRepository
import com.programmergabut.solatkuy.di.contextprovider.ContextProvider
import com.programmergabut.solatkuy.di.contextprovider.ContextProviderImpl
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.*
import java.util.*

class FakeQuranRepository constructor(
    private val msFavSurahDao: MsFavSurahDao,
    private val msSurahDao: MsSurahDao,
    private val msAyahDao: MsAyahDao,
    private val readSurahEnService: ReadSurahEnService,
    private val allSurahService: AllSurahService,
    private val readSurahArService: ReadSurahArService,
    private val contextProvider: ContextProvider,
):BaseRepository(), QuranRepository {

    /* MsFavSurah */
    override fun observeListFavSurah() = msFavSurahDao.observeFavSurahs()
    override fun observeFavSurahBySurahID(surahID: Int) = msFavSurahDao.observeFavSurahBySurahID(surahID)
    override suspend fun insertFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.insertMsSurah(msFavSurah)
    override suspend fun deleteFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.deleteMsFavSurah(msFavSurah)

    /* Remote */
    override suspend fun fetchReadSurahEn(surahID: Int): Deferred<Resource<ReadSurahEnResponse>> {
        return CoroutineScope(contextProvider.io()).async {
            lateinit var response: Resource<ReadSurahEnResponse>
            try {
                val result = execute(readSurahEnService.fetchReadSurahEn(surahID))
                response = Resource.success(result)
            }
            catch (ex: Exception){
                response = Resource.error(ReadSurahEnResponse())
                response.message = ex.message.toString()
            }
            response
        }
    }

    override suspend fun fetchAllSurah(): Deferred<Resource<AllSurahResponse>> {
        return CoroutineScope(contextProvider.io()).async {
            lateinit var response: Resource<AllSurahResponse>
            try {
                val result = execute(allSurahService.fetchAllSurah())
                response = Resource.success(result)
            }
            catch (ex: Exception){
                response = Resource.error(AllSurahResponse())
                response.message = ex.message.toString()
            }
            response
        }
    }

    override fun getAllSurah(): LiveData<Resource<List<MsSurah>>> {
        return object : NetworkBoundResource<List<MsSurah>, AllSurahResponse>(contextProvider) {
            override fun loadFromDB(): LiveData<List<MsSurah>> = msSurahDao.getSurahs()

            override fun shouldFetch(data: List<MsSurah>?): Boolean = data == null || data.isEmpty()

            override fun createCall(): LiveData<ApiResponse<AllSurahResponse>> {
                return liveData {
                    withContext(contextProvider.io()) {
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

    override suspend fun fetchReadSurahAr(surahID: Int): Deferred<Resource<ReadSurahArResponse>> {
        return CoroutineScope(contextProvider.io()).async {
            lateinit var response : Resource<ReadSurahArResponse>
            try {
                val result = execute(readSurahArService.fetchReadSurahAr(surahID))
                response = Resource.success(result)
            }
            catch (ex: Exception){
                response = Resource.error(ReadSurahArResponse())
                response.message = ex.message.toString()
            }
            response
        }
    }

    override fun getAyahBySurahID(surahID: Int): LiveData<Resource<List<MsAyah>>> {
        TODO("Not yet implemented")
    }

}