package com.programmergabut.solatkuy.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.verify
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.base.BaseRepository
import com.programmergabut.solatkuy.data.local.dao.MsAyahDao
import com.programmergabut.solatkuy.data.local.dao.MsFavSurahDao
import com.programmergabut.solatkuy.data.local.dao.MsSurahDao
import com.programmergabut.solatkuy.data.remote.api.AllSurahService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahArService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahEnService
import com.programmergabut.solatkuy.di.contextprovider.ContextProviderImpl
import com.programmergabut.solatkuy.di.contextprovider.ContextProviderTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class QuranRepositoryImplTest: BaseRepository() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()
    private val msFavSurahDao = mock(MsFavSurahDao::class.java)
    private val msSurahDao = mock(MsSurahDao::class.java)
    private val msAyahDao = mock(MsAyahDao::class.java)
    private val readSurahEnService = mock(ReadSurahEnService::class.java)
    private val allSurahService = mock(AllSurahService::class.java)
    private val readSurahArService = mock(ReadSurahArService::class.java)
    private val contextProvider = ContextProviderTest()
    private val quranRepository = FakeQuranRepository(msFavSurahDao, msSurahDao,
        msAyahDao, readSurahEnService,allSurahService, readSurahArService, contextProvider)
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

}