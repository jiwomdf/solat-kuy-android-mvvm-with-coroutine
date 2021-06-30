package com.programmergabut.solatkuy.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.verify
import com.programmergabut.solatkuy.CoroutineTestUtil.Companion.toDeferred
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.base.BaseRepository
import com.programmergabut.solatkuy.data.local.dao.MsAyahDao
import com.programmergabut.solatkuy.data.local.dao.MsFavAyahDao
import com.programmergabut.solatkuy.data.local.dao.MsFavSurahDao
import com.programmergabut.solatkuy.data.local.dao.MsSurahDao
import com.programmergabut.solatkuy.data.remote.api.AllSurahService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahArService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahEnService
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.ContextProviders
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.Call
import retrofit2.await

@ExperimentalCoroutinesApi
class QuranRepositoryImplTest: BaseRepository() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()
    private val msFavAyahDao = mock(MsFavAyahDao::class.java)
    private val msFavSurahDao = mock(MsFavSurahDao::class.java)
    private val msSurahDao = mock(MsSurahDao::class.java)
    private val msAyahDao = mock(MsAyahDao::class.java)
    private val readSurahEnService = mock(ReadSurahEnService::class.java)
    private val allSurahService = mock(AllSurahService::class.java)
    private val readSurahArService = mock(ReadSurahArService::class.java)
    private val contextProviders = mock(ContextProviders::class.java)
    private val quranRepository = FakeQuranRepository(msFavAyahDao, msFavSurahDao, msSurahDao,
        msAyahDao, readSurahEnService,allSurahService, readSurahArService,contextProviders)
    private val surahID = DummyRetValueTest.surahID

    /* Remote */
    @Test
    fun fetchReadSurahEn(): Unit = runBlocking {
        quranRepository.fetchReadSurahEn(surahID).await()
        verify(readSurahEnService).fetchReadSurahEn(surahID)
    }

    @Test
    fun fetchReadSurahAr(): Unit = runBlocking {
        quranRepository.fetchReadSurahAr(surahID).await()
        verify(readSurahArService).fetchReadSurahAr(surahID)
    }

    @Test
    fun fetchAllSurah(): Unit = runBlocking {
        quranRepository.fetchAllSurah().await()
        verify(allSurahService).fetchAllSurah()
    }


    /* Database */
    @Test
    fun getListFavAyah() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val listMsFavAyah = MutableLiveData(DummyRetValueTest.getListMsFavAyah())
        `when`(msFavAyahDao.observeListFavAyah()).thenReturn(listMsFavAyah)
        quranRepository.observeListFavAyah()
        verify(msFavAyahDao).observeListFavAyah()
        assertNotNull(listMsFavAyah)
    }

    @Test
    fun getListFavAyahBySurahID() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val listMsFavAyah = DummyRetValueTest.getListMsFavAyah()
        `when`(msFavAyahDao.getListFavAyahBySurahID(surahID)).thenReturn(listMsFavAyah)
        quranRepository.getListFavAyahBySurahID(surahID)
        verify(msFavAyahDao).getListFavAyahBySurahID(surahID)
        assertNotNull(listMsFavAyah)
    }
}