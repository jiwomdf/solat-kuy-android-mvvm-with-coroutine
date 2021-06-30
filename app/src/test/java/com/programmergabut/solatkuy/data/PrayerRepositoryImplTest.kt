package com.programmergabut.solatkuy.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.base.BaseRepository
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.api.PrayerApiService
import com.programmergabut.solatkuy.data.remote.api.QiblaApiService
import com.programmergabut.solatkuy.util.ContextProviders
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class PrayerRepositoryImplTest: BaseRepository(){

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    private val notifiedPrayerDao = mock(MsNotifiedPrayerDao::class.java)
    private val msApi1Dao = mock(MsApi1Dao::class.java)
    private val msSettingDao = mock(MsSettingDao::class.java)
    private val contextProviders = mock(ContextProviders::class.java)
    private val qiblaApiService = mock(QiblaApiService::class.java)
    private val prayerApiService = mock(PrayerApiService::class.java)
    private val msFavSurahDao = mock(MsFavSurahDao::class.java)
    private val msCalculationMethodsDao = mock(MsCalculationMethodsDao::class.java)
    private val prayerRepository = FakePrayerRepository(notifiedPrayerDao, msApi1Dao, msSettingDao,
        msCalculationMethodsDao, contextProviders, qiblaApiService, prayerApiService)
    private val msApi1 = DummyRetValueTest.msApi1
    private val msFavSurah = DummyRetValueTest.msfavSurah

    /* Remote */
    @Test
    fun fetchPrayerApi(): Unit = runBlocking {
        prayerRepository.fetchPrayerApi(msApi1).await()
        Mockito.verify(prayerApiService).fetchPrayer(msApi1.latitude,
            msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)
    }

   @Test
    fun fetchCompass(): Unit = runBlocking {
        prayerRepository.fetchQibla(msApi1).await()
        Mockito.verify(qiblaApiService).fetchQibla(msApi1.latitude, msApi1.longitude)
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