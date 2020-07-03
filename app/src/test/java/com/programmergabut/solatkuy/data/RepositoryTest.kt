package com.programmergabut.solatkuy.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyArgument
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhanImpl
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquranImpl
import com.programmergabut.solatkuy.DummyRetValue
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class RepositoryTest{
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    private val remoteDataSourceApiAlquran = mock(RemoteDataSourceApiAlquranImpl::class.java)
    private val remoteDataSourceAladhan = mock(RemoteDataSourceAladhanImpl::class.java)

    private val notifiedPrayerDao = mock(NotifiedPrayerDao::class.java)
    private val msApi1Dao = mock(MsApi1Dao::class.java)
    private val msSettingDao = mock(MsSettingDao::class.java)
    private val msFavAyahDao = mock(MsFavAyahDao::class.java)
    private val msFavSurahDao = mock(MsFavSurahDao::class.java)

    private val repository = FakeRepository(remoteDataSourceAladhan, remoteDataSourceApiAlquran, notifiedPrayerDao,
        msApi1Dao, msSettingDao, msFavAyahDao, msFavSurahDao)

    private val msApi1 = DummyArgument.msApi1
    private val msFavSurah = DummyArgument.msfavSurah
    private val surahID = DummyArgument.surahID
    private val prayerMap = DummyArgument.getMapPrayer()

    /* Remote */
    @Test
    fun fetchPrayerApi() = coroutinesTestRule.testDispatcher.runBlockingTest {
        repository.fetchPrayerApi(msApi1)

        val dummyPrayerApi = DummyRetValue.fetchPrayerApi()

        Mockito.`when`(remoteDataSourceAladhan.fetchPrayerApi(msApi1)).thenReturn(dummyPrayerApi)
        Mockito.verify(remoteDataSourceAladhan).fetchPrayerApi(msApi1)

        assertNotNull(dummyPrayerApi)
    }

    @Test
    fun fetchCompass() = coroutinesTestRule.testDispatcher.runBlockingTest {
        repository.fetchCompass(msApi1)

        val dummyCompassApi = DummyRetValue.fetchCompassApi()

        Mockito.`when`(remoteDataSourceAladhan.fetchCompassApi(msApi1)).thenReturn(dummyCompassApi)
        Mockito.verify(remoteDataSourceAladhan).fetchCompassApi(msApi1)

        assertNotNull(dummyCompassApi)
    }

    /* @Test
    fun fetchAsmaAlHusna(){
        repository.fetchAsmaAlHusna()

        val dummyAsmaAlHusna = Resource.success(DummyData.fetchAsmaAlHusnaApi())
        val asmaAlHusnaApi = MutableLiveData<Resource<AsmaAlHusnaApi>>()
        asmaAlHusnaApi.value = dummyAsmaAlHusna

        Mockito.`when`(remote.fetchAsmaAlHusnaApi()).thenReturn(asmaAlHusnaApi)
        Mockito.verify(remote).fetchAsmaAlHusnaApi()

        assertNotNull(asmaAlHusnaApi.value)
    } */

    @Test
    fun fetchReadSurahEn() = coroutinesTestRule.testDispatcher.runBlockingTest {
        repository.fetchReadSurahEn(surahID)

        val dummyQuranSurah = DummyRetValue.surahEnID_1()

        Mockito.`when`(remoteDataSourceApiAlquran.fetchReadSurahEn(surahID)).thenReturn(dummyQuranSurah)
        Mockito.verify(remoteDataSourceApiAlquran).fetchReadSurahEn(surahID)

        assertNotNull(dummyQuranSurah)
    }

    @Test
    fun fetchReadSurahAr() = coroutinesTestRule.testDispatcher.runBlockingTest {
        repository.fetchReadSurahAr(surahID)

        val dummyQuranSurah = DummyRetValue.surahArID_1()

        Mockito.`when`(remoteDataSourceApiAlquran.fetchReadSurahAr(surahID)).thenReturn(dummyQuranSurah)

        Mockito.verify(remoteDataSourceApiAlquran).fetchReadSurahAr(surahID)
        assertNotNull(dummyQuranSurah)
    }

    @Test
    fun fetchAllSurah() = coroutinesTestRule.testDispatcher.runBlockingTest {
        repository.fetchAllSurah()

        val dummyQuranSurah = DummyRetValue.fetchAllSurah()

        Mockito.`when`(remoteDataSourceApiAlquran.fetchAllSurah()).thenReturn(dummyQuranSurah)
        Mockito.verify(remoteDataSourceApiAlquran).fetchAllSurah()

        assertNotNull(dummyQuranSurah)
    }

    @Test
    fun syncNotifiedPrayer() = coroutinesTestRule.testDispatcher.runBlockingTest {
        val retVal = MutableLiveData<List<NotifiedPrayer>>()

        val prayerDummy = DummyRetValue.fetchPrayerApi()
        Mockito.`when`(remoteDataSourceAladhan.fetchPrayerApi(msApi1)).thenReturn(prayerDummy)
        val notifiedPrayerDummy = DummyRetValue.getNotifiedPrayer()
        Mockito.`when`(notifiedPrayerDao.getListNotifiedPrayerSync()).thenReturn(notifiedPrayerDummy)

        retVal.value = repository.syncNotifiedPrayer(msApi1)

        Mockito.verify(remoteDataSourceAladhan).fetchPrayerApi(msApi1)
        Mockito.verify(notifiedPrayerDao).getListNotifiedPrayerSync()

        assertNotNull(retVal)
    }


    /* Database */
    @Test
    fun getMsApi1(){
        repository.getMsApi1()

        val dummyMsApi1 = DummyRetValue.getMsApi1()
        val msApi1 = MutableLiveData<MsApi1>()
        msApi1.value = dummyMsApi1
        Mockito.`when`(msApi1Dao.getMsApi1()).thenReturn(msApi1)
        Mockito.verify(msApi1Dao).getMsApi1()

        assertNotNull(msApi1.value)
    }

    @Test
    fun updateMsApi1() = coroutinesTestRule.testDispatcher.runBlockingTest {
        msApi1Dao.updateMsApi1(1,msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)
        Mockito.verify(msApi1Dao).updateMsApi1(1,msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)
    }

    @Test
    fun getListFavAyah() {
        repository.getListFavAyah()

        val dummyMsFavAyah = DummyRetValue.getListMsFavAyah()
        val listMsFavAyah = MutableLiveData<List<MsFavAyah>>()
        listMsFavAyah.value = dummyMsFavAyah
        Mockito.`when`(msFavAyahDao.getListFavAyah()).thenReturn(listMsFavAyah)
        Mockito.verify(msFavAyahDao).getListFavAyah()

        assertNotNull(listMsFavAyah.value)
    }

    @Test
    fun getListFavAyahBySurahID() {
        repository.getListFavAyahBySurahID(surahID)

        val dummyMsFavAyah = DummyRetValue.getListMsFavAyah()
        val listMsFavAyah = MutableLiveData<List<MsFavAyah>>()
        listMsFavAyah.value = dummyMsFavAyah
        Mockito.`when`(msFavAyahDao.getListFavAyahBySurahID(surahID)).thenReturn(listMsFavAyah)
        Mockito.verify(msFavAyahDao).getListFavAyahBySurahID(surahID)

        assertNotNull(listMsFavAyah.value)
    }

    @Test
    fun insertFavSurah() = coroutinesTestRule.testDispatcher.runBlockingTest {
        msFavSurahDao.insertMsSurah(msFavSurah)
        Mockito.verify(msFavSurahDao).insertMsSurah(msFavSurah)
    }

    @Test
    fun deleteFavSurah() = coroutinesTestRule.testDispatcher.runBlockingTest {
        msFavSurahDao.deleteMsFavSurah(msFavSurah)
        Mockito.verify(msFavSurahDao).deleteMsFavSurah(msFavSurah)
    }
}