package com.programmergabut.solatkuy.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.base.BaseRepository
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.MsConfiguration
import com.programmergabut.solatkuy.data.remote.api.PrayerApiService
import com.programmergabut.solatkuy.data.remote.api.QiblaApiService
import com.programmergabut.solatkuy.di.contextprovider.ContextProviderTest
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
    private val msConfigurationDao = mock(MsConfigurationDao::class.java)
    private val msSettingDao = mock(MsSettingDao::class.java)
    private val contextProvider = ContextProviderTest()
    private val qiblaApiService = mock(QiblaApiService::class.java)
    private val prayerApiService = mock(PrayerApiService::class.java)
    private val msFavSurahDao = mock(MsFavSurahDao::class.java)
    private val msCalculationMethodsDao = mock(MsCalculationMethodsDao::class.java)
    private val prayerRepository = FakePrayerRepository(notifiedPrayerDao, msConfigurationDao, msSettingDao,
        msCalculationMethodsDao, contextProvider, qiblaApiService, prayerApiService)
    private val msConfiguration = DummyRetValueTest.msConfiguration
    private val msFavSurah = DummyRetValueTest.msfavSurah

    /* Remote */
    @Test
    fun fetchPrayerApi(): Unit = runBlocking {
        prayerRepository.fetchPrayerApi(msConfiguration).await()
        Mockito.verify(prayerApiService).fetchPrayer(msConfiguration.latitude,
            msConfiguration.longitude, msConfiguration.method, msConfiguration.month, msConfiguration.year)
    }

   @Test
    fun fetchCompass(): Unit = runBlocking {
        prayerRepository.fetchQibla(msConfiguration).await()
        Mockito.verify(qiblaApiService).fetchQibla(msConfiguration.latitude, msConfiguration.longitude)
    }

    /* Database */
    @Test
    fun getMsConfiguration(){
        prayerRepository.observeMsConfiguration()
        val dummyMsConfiguration = DummyRetValueTest.msConfiguration
        val msConfiguration = MutableLiveData<MsConfiguration>()
        msConfiguration.value = dummyMsConfiguration
        Mockito.`when`(msConfigurationDao.observeMsConfiguration()).thenReturn(msConfiguration)
        Mockito.verify(msConfigurationDao).observeMsConfiguration()
        assertNotNull(msConfiguration.value)
    }

    @Test
    fun updateMsConfiguration() = coroutinesTestRule.testDispatcher.runBlockingTest {
        msConfigurationDao.updateMsConfiguration(1,msConfiguration.latitude, msConfiguration.longitude, msConfiguration.method, msConfiguration.month, msConfiguration.year)
        Mockito.verify(msConfigurationDao).updateMsConfiguration(1,msConfiguration.latitude, msConfiguration.longitude, msConfiguration.method, msConfiguration.month, msConfiguration.year)
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