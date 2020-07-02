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
    private val surahID = DummyArgument.surahID

    @Test
    fun fetchPrayerApi() = coroutinesTestRule.testDispatcher.runBlockingTest {
        repository.fetchPrayerApi(msApi1)

        val dummyPrayerApi = DummyRetValue.fetchPrayerApi()

        /* val prayerApi = MutableLiveData<PrayerResponse>()
        prayerApi.value = dummyPrayerApi */

        Mockito.`when`(remoteDataSourceAladhan.fetchPrayerApi(msApi1)).thenReturn(dummyPrayerApi)
        Mockito.verify(remoteDataSourceAladhan).fetchPrayerApi(msApi1)

        assertNotNull(dummyPrayerApi)
    }

    @Test
    fun fetchCompass() = coroutinesTestRule.testDispatcher.runBlockingTest {
        repository.fetchCompass(msApi1)

        val dummyCompassApi = DummyRetValue.fetchCompassApi()
        /* val compassApi = MutableLiveData<Resource<CompassResponse>>()
        compassApi.value = dummyCompassApi */

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
    fun fetchQuranSurah() = coroutinesTestRule.testDispatcher.runBlockingTest {
        repository.fetchReadSurahEn(surahID)

        val dummyQuranSurah = DummyRetValue.surahEnID_1()
        /* val quranSurahApi = MutableLiveData<Resource<ReadSurahEnResponse>>()
        quranSurahApi.value = dummyQuranSurah */

        Mockito.`when`(remoteDataSourceApiAlquran.fetchReadSurahEn(surahID)).thenReturn(dummyQuranSurah)
        Mockito.verify(remoteDataSourceApiAlquran).fetchReadSurahEn(surahID)

        assertNotNull(dummyQuranSurah)
    }

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


}