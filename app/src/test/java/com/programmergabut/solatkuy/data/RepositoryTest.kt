package com.programmergabut.solatkuy.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.local.LocalDataSource
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhan
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquran
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.generator.DummyData
import junit.framework.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class RepositoryTest{
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val remoteDataSourceApiAlquran = mock(RemoteDataSourceApiAlquran::class.java)
    private val remoteDataSourceAladhan = mock(RemoteDataSourceAladhan::class.java)
    private val local = mock(LocalDataSource::class.java)
    private val repository = FakeRepository(remoteDataSourceAladhan, remoteDataSourceApiAlquran, local)

    private val msApi1 = MsApi1(0, "", "", "","","")


    @Test
    fun fetchPrayerApi(){
        repository.fetchPrayerApi(msApi1)

        val dummyPrayerApi = Resource.success(DummyData.fetchPrayerApi())
        val prayerApi = MutableLiveData<Resource<PrayerResponse>>()
        prayerApi.value = dummyPrayerApi

        Mockito.`when`(remoteDataSourceAladhan.fetchPrayerApi(msApi1)).thenReturn(prayerApi)
        Mockito.verify(remoteDataSourceAladhan).fetchPrayerApi(msApi1)

        assertNotNull(prayerApi.value)
    }

    @Test
    fun fetchCompass(){
        repository.fetchCompass(msApi1)

        val dummyCompassApi = Resource.success(DummyData.fetchCompassApi())
        val compassApi = MutableLiveData<Resource<CompassResponse>>()
        compassApi.value = dummyCompassApi

        Mockito.`when`(remoteDataSourceAladhan.fetchCompassApi(msApi1)).thenReturn(compassApi)
        Mockito.verify(remoteDataSourceAladhan).fetchCompassApi(msApi1)

        assertNotNull(compassApi.value)
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
    fun fetchQuranSurah(){
        repository.fetchReadSurahEn(1)

        val dummyQuranSurah = Resource.success(DummyData.fetchSurahApi())
        val quranSurahApi = MutableLiveData<Resource<ReadSurahEnResponse>>()
        quranSurahApi.value = dummyQuranSurah

        Mockito.`when`(remoteDataSourceApiAlquran.fetchReadSurahEn(1)).thenReturn(quranSurahApi)
        Mockito.verify(remoteDataSourceApiAlquran).fetchReadSurahEn(1)

        assertNotNull(quranSurahApi.value)
    }

    @Test
    fun getMsApi1(){
        repository.getMsApi1()

        val dummyMsApi1 = DummyData.getMsApi1()
        val msApi1 = MutableLiveData<MsApi1>()
        msApi1.value = dummyMsApi1
        Mockito.`when`(local.getMsApi1()).thenReturn(msApi1)
        Mockito.verify(local).getMsApi1()

        assertNotNull(msApi1.value)
    }


}