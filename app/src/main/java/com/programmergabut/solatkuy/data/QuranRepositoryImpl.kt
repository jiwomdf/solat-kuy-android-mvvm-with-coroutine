package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.programmergabut.solatkuy.data.local.dao.MsAyahDao
import com.programmergabut.solatkuy.data.local.dao.MsSurahDao
import com.programmergabut.solatkuy.data.local.localentity.MsAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.local.localentity.MsSurah
import com.programmergabut.solatkuy.data.remote.ApiResponse
import com.programmergabut.solatkuy.data.remote.api.AllSurahService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahArService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahEnService
import com.programmergabut.solatkuy.data.remote.json.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonAr.Ayah
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.ContextProviders
import com.programmergabut.solatkuy.base.BaseRepository
import com.programmergabut.solatkuy.data.local.dao.MsFavSurahDao
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class QuranRepositoryImpl @Inject constructor(
    private val msFavSurahDao: MsFavSurahDao,
    private val msSurahDao: MsSurahDao,
    private val msAyahDao: MsAyahDao,
    private val readSurahEnService: ReadSurahEnService,
    private val allSurahService: AllSurahService,
    private val readSurahArService: ReadSurahArService,
    private val contextProviders: ContextProviders,
): BaseRepository(), QuranRepository {

    /* MsFavSurah */
    override fun observeListFavSurah(): LiveData<List<MsFavSurah>> = msFavSurahDao.observeFavSurahs()
    override fun observeFavSurahBySurahID(surahID: Int): LiveData<MsFavSurah?> =
        msFavSurahDao.observeFavSurahBySurahID(surahID)
    override suspend fun insertFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.insertMsSurah(msFavSurah)
    override suspend fun deleteFavSurah(msFavSurah: MsFavSurah) = msFavSurahDao.deleteMsFavSurah(msFavSurah)

    /* Remote */
    override suspend fun fetchReadSurahEn(surahID: Int): Deferred<ReadSurahEnResponse> {
        return CoroutineScope(contextProviders.IO).async {
            lateinit var response: ReadSurahEnResponse
            try {
                response = execute(readSurahEnService.fetchReadSurahEn(surahID))
                response.responseStatus = "1"
            }
            catch (ex: Exception){
                response = ReadSurahEnResponse()
                response.responseStatus = "-1"
                response.message = ex.message.toString()
            }
            response
        }
    }

    override suspend fun fetchAllSurah(): Deferred<AllSurahResponse> {
        return CoroutineScope(contextProviders.IO).async {
            lateinit var response: AllSurahResponse
            try {
                response = execute(allSurahService.fetchAllSurah())
                response.responseStatus = "1"
            }
            catch (ex: Exception){
                response = AllSurahResponse()
                response.responseStatus = "-1"
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
                    val lowerEngName = it.englishName.lowercase(Locale.ROOT).replace("-"," ").trim()
                    MsSurah(
                        englishName = it.englishName,
                        englishNameLowerCase = lowerEngName,
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
        return CoroutineScope(contextProviders.IO).async {
            lateinit var response : ReadSurahArResponse
            try {
                response = execute(readSurahArService.fetchReadSurahAr(surahID))
                response.responseStatus = "1"
            }
            catch (ex: Exception){
                response = ReadSurahArResponse()
                response.responseStatus = "-1"
                response.message = ex.message.toString()
            }
            response
        }
    }

    override fun getAyahBySurahID(surahID: Int): LiveData<Resource<List<MsAyah>>> {
        return object : NetworkBoundResource<List<MsAyah>, ReadSurahArResponse>(contextProviders) {
            override fun loadFromDB(): LiveData<List<MsAyah>> = msAyahDao.getAyahsBySurahID(surahID)

            override fun shouldFetch(data: List<MsAyah>?): Boolean = data == null || data.isEmpty()

            override fun createCall(): LiveData<ApiResponse<ReadSurahArResponse>> {
                return liveData {
                    withContext(contextProviders.IO) {
                        lateinit var arResponse: ReadSurahArResponse
                        lateinit var enResponse: ReadSurahEnResponse
                        try {
                            val hashMapOfAyah = hashMapOf<Int, Ayah>()
                            arResponse = execute(readSurahArService.fetchReadSurahAr(surahID))
                            enResponse = execute(readSurahEnService.fetchReadSurahEn(surahID))

                            for(i in arResponse.data.ayahs.indices){
                                if(hashMapOfAyah[i] == null)
                                    hashMapOfAyah[i] = arResponse.data.ayahs[i]
                            }

                            for(i in enResponse.data.ayahs.indices){
                                if(hashMapOfAyah[i] != null)
                                    hashMapOfAyah[i]?.textEn = enResponse.data.ayahs[i].text
                            }

                            arResponse.data.ayahs = hashMapOfAyah.values.toList()
                            emit(ApiResponse.success(arResponse))
                        } catch (ex: Exception) {
                            arResponse = ReadSurahArResponse()
                            arResponse.message = ex.message.toString()
                            emit(ApiResponse.error(ex.message.toString(), arResponse))
                        }
                    }
                }
            }

            override fun saveCallResult(data: ReadSurahArResponse) {

                val ayahs = data.data.let { result ->
                    result.ayahs.map { ayah ->
                        MsAyah(
                            hizbQuarter = ayah.hizbQuarter,
                            juz = ayah.juz,
                            manzil = ayah.manzil,
                            number = ayah.number,
                            numberInSurah = ayah.numberInSurah,
                            page = ayah.page,
                            ruku = ayah.ruku,
                            text = ayah.text,
                            textEn = ayah.textEn,
                            englishName = result.englishName,
                            englishNameTranslation = result.englishNameTranslation,
                            name = result.name,
                            numberOfAyahs = result.numberOfAyahs,
                            revelationType = result.revelationType,
                            surahID = result.number
                        )
                    }
                }

                ayahs?.first()?.let {
                    msAyahDao.deleteAyahsBySurahID(it.number)
                    msAyahDao.insertAyahs(ayahs)
                }
            }

        }.asLiveData()
    }

}