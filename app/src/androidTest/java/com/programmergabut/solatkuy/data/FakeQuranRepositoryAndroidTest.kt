package com.programmergabut.solatkuy.data

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.json.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.DummyValueAndroidTest
import com.programmergabut.solatkuy.data.local.localentity.MsAyah
import com.programmergabut.solatkuy.data.local.localentity.MsSurah
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonAr.Ayah
import com.programmergabut.solatkuy.util.Resource
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

@HiltAndroidTest
class FakeQuranRepositoryAndroidTest : com.programmergabut.solatkuy.quran.data.QuranRepository {

    private var listMsFavSurah = DummyValueAndroidTest.getListMsFavSurah()
    private val observableMsFavSurahs = MutableLiveData<List<MsFavSurah>>()
    private val observableMsFavSurah = MutableLiveData<MsFavSurah>()
    private fun refreshMsFavSurah() {
        observableMsFavSurahs.postValue(listMsFavSurah)
        observableMsFavSurah.value = listMsFavSurah[0]
    }

    /* MsFavSurah */
    override fun observeListFavSurah(): LiveData<List<MsFavSurah>> {
        return observableMsFavSurahs
    }

    override fun observeFavSurahBySurahID(surahID: Int): LiveData<MsFavSurah?> {
        return observableMsFavSurah
    }

    override suspend fun insertFavSurah(msFavSurah: MsFavSurah) {
        listMsFavSurah.add(msFavSurah)
        refreshMsFavSurah()
    }

    override suspend fun deleteFavSurah(msFavSurah: MsFavSurah) {
        listMsFavSurah.add(msFavSurah)
        refreshMsFavSurah()
    }

    /* Retrofit */
    override suspend fun fetchReadSurahEn(surahID: Int): Deferred<Resource<ReadSurahEnResponse>> {
        return CoroutineScope(IO).async {
            val data = DummyValueAndroidTest.surahEnID_1<FakeQuranRepositoryAndroidTest>()
            data.message = "testing"
            Resource.success(data)
        }
    }

    override suspend fun fetchAllSurah(): Deferred<Resource<AllSurahResponse>> {
        return CoroutineScope(IO).async {
            val data = DummyValueAndroidTest.fetchAllSurah<FakeQuranRepositoryAndroidTest>()
            data.message = "testing"
            Resource.success(data)
        }
    }

    override fun getAllSurah(): LiveData<Resource<List<MsSurah>>> {
        val response = DummyValueAndroidTest.fetchAllSurah<FakeQuranRepositoryAndroidTest>()
        val newResponse = response.data.map {
            MsSurah(
                number = it.number,
                englishName = it.englishName,
                englishNameLowerCase = it.englishName.toLowerCase(),
                englishNameTranslation = it.englishNameTranslation,
                name = it.name,
                numberOfAyahs = it.numberOfAyahs,
                revelationType = it.revelationType,
            )
        }
        return liveData {
            emit(
                Resource.success(
                    newResponse
                )
            )
        }
    }

    override suspend fun fetchReadSurahAr(surahID: Int): Deferred<Resource<ReadSurahArResponse>> {
        return CoroutineScope(IO).async {
            val data = DummyValueAndroidTest.surahArID_1<FakeQuranRepositoryAndroidTest>()
            data.message = "testing"
            Resource.success(data)
        }
    }

    override fun getAyahBySurahID(surahID: Int): LiveData<Resource<List<MsAyah>>> {
        val hashMapOfAyah = hashMapOf<Int, Ayah>()
        val arResponse = DummyValueAndroidTest.surahArID_1<FakeQuranRepositoryAndroidTest>()
        val enResponse = DummyValueAndroidTest.surahEnID_1<FakeQuranRepositoryAndroidTest>()

        for (i in arResponse.data.ayahs.indices) {
            if (hashMapOfAyah[i] == null)
                hashMapOfAyah[i] = arResponse.data.ayahs[i]
        }

        for (i in enResponse.data.ayahs.indices) {
            if (hashMapOfAyah[i] != null)
                hashMapOfAyah[i]?.textEn = enResponse.data.ayahs[i].text
        }

        arResponse.data.ayahs = hashMapOfAyah.values.toList()

        var idx = 1
        val newList = hashMapOfAyah.values.map {
            return@map MsAyah(
                hizbQuarter = it.hizbQuarter,
                juz = it.juz,
                manzil = it.manzil,
                number = it.number,
                numberInSurah = it.numberInSurah,
                page = it.page,
                ruku = it.ruku,
                text = it.text,
                englishName = arResponse.data.englishName,
                englishNameTranslation = arResponse.data.englishNameTranslation,
                name = arResponse.data.name,
                numberOfAyahs = arResponse.data.numberOfAyahs,
                revelationType = arResponse.data.revelationType,
                textEn = it.textEn,
                isFav = it.isFav,
                isLastRead = it.isLastRead,
                surahID = 114,
            ).also { ayah ->
                ayah.ayahID = idx
                idx++
            }
        }

        return liveData {
            emit(Resource.success(newList))
        }
    }
}