package com.programmergabut.solatkuy.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.data.local.dao.MsApi1Dao
import com.programmergabut.solatkuy.data.local.dao.MsFavSurahDao
import com.programmergabut.solatkuy.data.local.dao.MsSettingDao
import com.programmergabut.solatkuy.data.local.dao.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhan
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class PrayerRepositoryImplTest{

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()
    private val remoteDataSourceAladhan = mock(RemoteDataSourceAladhan::class.java)
    private val notifiedPrayerDao = mock(NotifiedPrayerDao::class.java)
    private val msApi1Dao = mock(MsApi1Dao::class.java)
    private val msSettingDao = mock(MsSettingDao::class.java)
    private val msFavSurahDao = mock(MsFavSurahDao::class.java)
    private val prayerRepository = FakePrayerRepository(remoteDataSourceAladhan, notifiedPrayerDao, msApi1Dao, msSettingDao)
    private val msApi1 = DummyRetValueTest.msApi1
    private val msFavSurah = DummyRetValueTest.msfavSurah

    /* Remote */
    @Test
    fun fetchPrayerApi() = runBlocking {
        val dummyPrayerApi = DummyRetValueTest.fetchPrayerApi<PrayerRepositoryImplTest>()
        Mockito.`when`(remoteDataSourceAladhan.fetchPrayerApi(msApi1)).thenReturn(dummyPrayerApi)
        prayerRepository.fetchPrayerApi(msApi1).await()
        Mockito.verify(remoteDataSourceAladhan).fetchPrayerApi(msApi1)
        assertNotNull(dummyPrayerApi)
    }

    @Test
    fun fetchCompass() = runBlocking {
        val dummyCompassApi = DummyRetValueTest.fetchCompassApi<PrayerRepositoryImplTest>()
        Mockito.`when`(remoteDataSourceAladhan.fetchCompassApi(msApi1)).thenReturn(dummyCompassApi)
        prayerRepository.fetchCompass(msApi1).await()
        Mockito.verify(remoteDataSourceAladhan).fetchCompassApi(msApi1)
        assertNotNull(dummyCompassApi)
    }

    /* Database */
    @Test
    fun getMsApi1(){
        prayerRepository.observeMsApi1()
        val dummyMsApi1 = DummyRetValueTest.msApi1
        val msApi1 = MutableLiveData<MsApi1>()
        msApi1.value = dummyMsApi1
        Mockito.`when`(msApi1Dao.observeMsApi1()).thenReturn(msApi1)
        Mockito.verify(msApi1Dao).observeMsApi1()
        assertNotNull(msApi1.value)
    }

    @Test
    fun updateMsApi1() = coroutinesTestRule.testDispatcher.runBlockingTest {
        msApi1Dao.updateMsApi1(1,msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)
        Mockito.verify(msApi1Dao).updateMsApi1(1,msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)
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