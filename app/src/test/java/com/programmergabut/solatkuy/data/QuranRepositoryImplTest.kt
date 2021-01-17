package com.programmergabut.solatkuy.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.verify
import com.programmergabut.solatkuy.CoroutineTestUtil.Companion.toDeferred
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyArgument
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.remote.FakeRemoteDataSourceAlQuran
import com.programmergabut.solatkuy.data.remote.api.ReadSurahArService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahEnService
import junit.framework.Assert
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class QuranRepositoryImplTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    private val msFavAyahDao = mock(MsFavAyahDao::class.java)
    private val msFavSurahDao = mock(MsFavSurahDao::class.java)
    private val remoteDataSourceApiAlquran = mock(FakeRemoteDataSourceAlQuran::class.java)
    private val quranRepository = FakeQuranRepository(remoteDataSourceApiAlquran, msFavAyahDao, msFavSurahDao)
    private val surahID = DummyArgument.surahID

    @Before
    fun setup(){
        //prayerRepository.getMsApi1()
    }

    /* Remote */
    @Test
    fun fetchReadSurahEn() = coroutinesTestRule.testDispatcher.runBlockingTest {

        quranRepository.fetchReadSurahEn(surahID).toDeferred()

        val dummyQuranSurah = DummyRetValueTest.surahEnID_1<QuranRepositoryImplTest>()
        `when`(remoteDataSourceApiAlquran.fetchReadSurahEn(surahID)).thenReturn(dummyQuranSurah)
        verify(remoteDataSourceApiAlquran).fetchReadSurahEn(surahID)

        assertNotNull(dummyQuranSurah)
    }

    @Test
    fun fetchReadSurahAr() = coroutinesTestRule.testDispatcher.runBlockingTest {

        quranRepository.fetchReadSurahAr(surahID).toDeferred()

        val dummyQuranSurah = DummyRetValueTest.surahArID_1<QuranRepositoryImplTest>()
        `when`(remoteDataSourceApiAlquran.fetchReadSurahAr(surahID)).thenReturn(dummyQuranSurah)
        verify(remoteDataSourceApiAlquran).fetchReadSurahAr(surahID)

        assertNotNull(dummyQuranSurah)
    }

    @Test
    fun fetchAllSurah() = coroutinesTestRule.testDispatcher.runBlockingTest {

        quranRepository.fetchAllSurah().toDeferred()

        val dummyQuranSurah = DummyRetValueTest.fetchAllSurahAr<QuranRepositoryImplTest>()
        `when`(remoteDataSourceApiAlquran.fetchAllSurah()).thenReturn(dummyQuranSurah)
        verify(remoteDataSourceApiAlquran).fetchAllSurah()

        assertNotNull(dummyQuranSurah)
    }


    /* Database */
    @Test
    fun getListFavAyah() = coroutinesTestRule.testDispatcher.runBlockingTest{

        val listMsFavAyah = MutableLiveData(DummyRetValueTest.getListMsFavAyah())
        `when`(msFavAyahDao.getListFavAyah()).thenReturn(listMsFavAyah)
        quranRepository.getListFavAyah()
        verify(msFavAyahDao).getListFavAyah()

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