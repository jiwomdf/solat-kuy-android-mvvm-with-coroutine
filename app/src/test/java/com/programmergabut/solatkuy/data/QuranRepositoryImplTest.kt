package com.programmergabut.solatkuy.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.CoroutineTestUtil.Companion.toDeferred
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyArgument
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.remote.FakeRemoteDataSourceAlQuran
import junit.framework.Assert
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class QuranRepositoryImplTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    private val fakeRemoteDataSourceAlQuran = mock(FakeRemoteDataSourceAlQuran::class.java)
    private val msFavAyahDao = mock(MsFavAyahDao::class.java)
    private val msFavSurahDao = mock(MsFavSurahDao::class.java)
    private val quranRepository = FakeQuranRepository(fakeRemoteDataSourceAlQuran, msFavAyahDao, msFavSurahDao)
    private val surahID = DummyArgument.surahID

    @Before
    fun setup(){
        //prayerRepository.getMsApi1()
    }

    /* Remote */
    @Test
    fun fetchReadSurahEn() = coroutinesTestRule.testDispatcher.runBlockingTest {
        val dummyQuranSurah = DummyRetValueTest.surahEnID_1()

        Mockito.`when`(fakeRemoteDataSourceAlQuran.fetchReadSurahEn(surahID)).thenReturn(dummyQuranSurah)
        quranRepository.fetchReadSurahEn(surahID).toDeferred()
        Mockito.verify(fakeRemoteDataSourceAlQuran).fetchReadSurahEn(surahID)

        Assert.assertNotNull(dummyQuranSurah)
    }

    @Test
    fun fetchReadSurahAr() = coroutinesTestRule.testDispatcher.runBlockingTest {
        val dummyQuranSurah = DummyRetValueTest.surahArID_1()

        Mockito.`when`(fakeRemoteDataSourceAlQuran.fetchReadSurahAr(surahID)).thenReturn(dummyQuranSurah)
        quranRepository.fetchReadSurahAr(surahID).toDeferred()
        Mockito.verify(fakeRemoteDataSourceAlQuran).fetchReadSurahAr(surahID)

        Assert.assertNotNull(dummyQuranSurah)
    }

    @Test
    fun fetchAllSurah() = coroutinesTestRule.testDispatcher.runBlockingTest {
        val dummyQuranSurah = DummyRetValueTest.fetchAllSurah()

        Mockito.`when`(fakeRemoteDataSourceAlQuran.fetchAllSurah()).thenReturn(dummyQuranSurah)
        quranRepository.fetchAllSurah().toDeferred()
        Mockito.verify(fakeRemoteDataSourceAlQuran).fetchAllSurah()

        Assert.assertNotNull(dummyQuranSurah)
    }


    /* Database */
    @Test
    fun getListFavAyah() {

        val dummyMsFavAyah = DummyRetValueTest.getListMsFavAyah()
        val listMsFavAyah = MutableLiveData<List<MsFavAyah>>()
        listMsFavAyah.value = dummyMsFavAyah
        Mockito.`when`(msFavAyahDao.getListFavAyah()).thenReturn(listMsFavAyah)
        quranRepository.getListFavAyah()
        Mockito.verify(msFavAyahDao).getListFavAyah()

        Assert.assertNotNull(listMsFavAyah.value)
    }

    @Test
    fun getListFavAyahBySurahID() {
        val dummyMsFavAyah = DummyRetValueTest.getListMsFavAyah()
        val listMsFavAyah = MutableLiveData<List<MsFavAyah>>()
        listMsFavAyah.value = dummyMsFavAyah
        Mockito.`when`(msFavAyahDao.getListFavAyahBySurahID(surahID)).thenReturn(listMsFavAyah)
        quranRepository.getListFavAyahBySurahID(surahID)
        Mockito.verify(msFavAyahDao).getListFavAyahBySurahID(surahID)

        Assert.assertNotNull(listMsFavAyah.value)
    }
}