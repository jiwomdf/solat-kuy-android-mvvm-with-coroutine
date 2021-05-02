package com.programmergabut.solatkuy.data

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
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
class FakeQuranRepositoryAndroidTest : QuranRepository {

    private var listMsFavAyah = DummyValueAndroidTest.getListMsFavAyah()
    private val observableMsFavAyahs = MutableLiveData<List<MsFavAyah>>()
    private fun refreshMsFavAyah() {
        observableMsFavAyahs.postValue(listMsFavAyah)
    }

    private var listMsFavSurah = DummyValueAndroidTest.getListMsFavSurah()
    private val observableMsFavSurahs = MutableLiveData<List<MsFavSurah>>()
    private val observableMsFavSurah = MutableLiveData<MsFavSurah>()
    private fun refreshMsFavSurah() {
        observableMsFavSurahs.postValue(listMsFavSurah)
        observableMsFavSurah.value = listMsFavSurah[0]
    }

    /* Room */
    /* MsFavAyah */
    override fun observeListFavAyah(): LiveData<List<MsFavAyah>> {
        return observableMsFavAyahs
    }

    override suspend fun getListFavAyahBySurahID(surahID: Int): List<MsFavAyah>? {
        return listMsFavAyah
    }

    override suspend fun insertFavAyah(msFavAyah: MsFavAyah) {
        listMsFavAyah.add(msFavAyah)
        refreshMsFavAyah()
    }

    override suspend fun deleteFavAyah(msFavAyah: MsFavAyah) {
        listMsFavAyah.remove(msFavAyah)
        refreshMsFavAyah()
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
    override suspend fun fetchReadSurahEn(surahID: Int): Deferred<ReadSurahEnResponse> {
        return CoroutineScope(IO).async {
            val data = DummyValueAndroidTest.surahEnID_1<FakeQuranRepositoryAndroidTest>()
            data.responseStatus = "1"
            data.message = "testing"
            data
        }
    }

    override suspend fun fetchAllSurah(): Deferred<AllSurahResponse> {
        return CoroutineScope(IO).async {
            val data = DummyValueAndroidTest.fetchAllSurah<FakeQuranRepositoryAndroidTest>()
            data.responseStatus = "1"
            data.message = "testing"
            data
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

    override suspend fun fetchReadSurahAr(surahID: Int): Deferred<ReadSurahArResponse> {
        return CoroutineScope(IO).async {
            val data = DummyValueAndroidTest.surahArID_1<FakeQuranRepositoryAndroidTest>()
            data.responseStatus = "1"
            data.message = "testing"
            data
        }
    }

    override fun getSelectedSurah(surahID: Int): LiveData<Resource<List<MsAyah>>> {
        val hashMapOfAyah = hashMapOf<Int, Ayah>()
        val arResponse = DummyValueAndroidTest.surahArID_1<FakeQuranRepositoryAndroidTest>()
        val enResponse = DummyValueAndroidTest.surahEnID_1<FakeQuranRepositoryAndroidTest>()

        for (i in 0 until arResponse.data.ayahs.size - 1) {
            if (hashMapOfAyah[i] == null)
                hashMapOfAyah[i] = arResponse.data.ayahs[i]
        }

        for (i in 0 until enResponse.data.ayahs.size - 1) {
            if (hashMapOfAyah[i] != null)
                hashMapOfAyah[i]?.textEn = enResponse.data.ayahs[i].text
        }

        arResponse.data.ayahs = hashMapOfAyah.values.toList()

        val newResponse =
            MsAyah(
                hizbQuarter = arResponse.data.ayahs[0].hizbQuarter,
                juz = arResponse.data.ayahs[0].juz,
                manzil = arResponse.data.ayahs[0].manzil,
                number = arResponse.data.ayahs[0].number,
                numberInSurah = arResponse.data.ayahs[0].numberInSurah,
                page = arResponse.data.ayahs[0].page,
                ruku = arResponse.data.ayahs[0].ruku,
                text = arResponse.data.ayahs[0].text,
                englishName = arResponse.data.englishName,
                englishNameTranslation = arResponse.data.englishNameTranslation,
                name = arResponse.data.name,
                numberOfAyahs = arResponse.data.numberOfAyahs,
                revelationType = arResponse.data.revelationType,
                textEn = arResponse.data.ayahs[0].textEn,
                isFav = arResponse.data.ayahs[0].isFav,
                isLastRead = arResponse.data.ayahs[0].isLastRead,
                surahID = 1
            )

        return liveData {
            emit(
                Resource.success(listOf(newResponse)
            ))
        }
    }
}