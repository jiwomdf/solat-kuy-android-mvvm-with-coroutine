package com.programmergabut.solatkuy.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyArgument
import com.programmergabut.solatkuy.DummyRetValue
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhanImpl
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquranImpl
import junit.framework.Assert
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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

    private val remoteDataSourceApiAlquran = mock(RemoteDataSourceApiAlquranImpl::class.java)
    private val msFavAyahDao = mock(MsFavAyahDao::class.java)
    private val msFavSurahDao = mock(MsFavSurahDao::class.java)
    private val prayerRepository = FakeQuranRepository(remoteDataSourceApiAlquran, msFavAyahDao, msFavSurahDao)
    private val surahID = DummyArgument.surahID


    /* Remote */
    @Test
    fun fetchReadSurahEn() = coroutinesTestRule.testDispatcher.runBlockingTest {
        prayerRepository.fetchReadSurahEn(surahID)

        val dummyQuranSurah = DummyRetValue.surahEnID_1()

        Mockito.`when`(remoteDataSourceApiAlquran.fetchReadSurahEn(surahID)).thenReturn(dummyQuranSurah)
        Mockito.verify(remoteDataSourceApiAlquran).fetchReadSurahEn(surahID)

        Assert.assertNotNull(dummyQuranSurah)
    }

    @Test
    fun fetchReadSurahAr() = coroutinesTestRule.testDispatcher.runBlockingTest {
        prayerRepository.fetchReadSurahAr(surahID)

        val dummyQuranSurah = DummyRetValue.surahArID_1()

        Mockito.`when`(remoteDataSourceApiAlquran.fetchReadSurahAr(surahID)).thenReturn(dummyQuranSurah)

        Mockito.verify(remoteDataSourceApiAlquran).fetchReadSurahAr(surahID)
        Assert.assertNotNull(dummyQuranSurah)
    }

    @Test
    fun fetchAllSurah() = coroutinesTestRule.testDispatcher.runBlockingTest {
        prayerRepository.fetchAllSurah()

        val dummyQuranSurah = DummyRetValue.fetchAllSurah()

        Mockito.`when`(remoteDataSourceApiAlquran.fetchAllSurah()).thenReturn(dummyQuranSurah)
        Mockito.verify(remoteDataSourceApiAlquran).fetchAllSurah()

        Assert.assertNotNull(dummyQuranSurah)
    }


    /* Database */
    @Test
    fun getListFavAyah() {
        prayerRepository.getListFavAyah()

        val dummyMsFavAyah = DummyRetValue.getListMsFavAyah()
        val listMsFavAyah = MutableLiveData<List<MsFavAyah>>()
        listMsFavAyah.value = dummyMsFavAyah
        Mockito.`when`(msFavAyahDao.getListFavAyah()).thenReturn(listMsFavAyah)
        Mockito.verify(msFavAyahDao).getListFavAyah()

        Assert.assertNotNull(listMsFavAyah.value)
    }

    @Test
    fun getListFavAyahBySurahID() {
        prayerRepository.getListFavAyahBySurahID(surahID)

        val dummyMsFavAyah = DummyRetValue.getListMsFavAyah()
        val listMsFavAyah = MutableLiveData<List<MsFavAyah>>()
        listMsFavAyah.value = dummyMsFavAyah
        Mockito.`when`(msFavAyahDao.getListFavAyahBySurahID(surahID)).thenReturn(listMsFavAyah)
        Mockito.verify(msFavAyahDao).getListFavAyahBySurahID(surahID)

        Assert.assertNotNull(listMsFavAyah.value)
    }
}