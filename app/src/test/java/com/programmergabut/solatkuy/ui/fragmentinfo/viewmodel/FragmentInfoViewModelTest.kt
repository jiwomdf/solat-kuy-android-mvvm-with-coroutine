package com.programmergabut.solatkuy.ui.fragmentinfo.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.ui.fragmentinfo.FragmentInfoViewModel
import com.programmergabut.solatkuy.ui.fragmentsetting.FragmentSettingViewModel
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.generator.DummyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FragmentInfoViewModelTest {

    private lateinit var viewModel: FragmentInfoViewModel

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository

    private val msApi1 = MsApi1(0, "", "", "","","")

    @ExperimentalCoroutinesApi
    val dispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = FragmentInfoViewModel(repository)

        viewModel.fetchPrayerApi(msApi1)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchPrayerApi() = runBlockingTest{
        val observer = mock<Observer<Resource<PrayerResponse>>>()
        val dummyPrayerApi = Resource.success(DummyData.fetchPrayerApi())

        /* val prayerApi = MutableLiveData<Resource<PrayerResponse>>()
        prayerApi.value = dummyPrayerApi */

        `when`(repository.fetchPrayerApi(msApi1)).thenReturn(dummyPrayerApi.data)

        viewModel.prayer.observeForever(observer)

        Mockito.verify(observer).onChanged(dummyPrayerApi)
    }

   /* @Test
    fun fetchAsmaAlHusna(){
        val observer = mock<Observer<Resource<AsmaAlHusnaApi>>>()
        val dummyAsmaAlHusna = Resource.success(DummyData.fetchAsmaAlHusnaApi())
        val asmaAlHusna = MutableLiveData<Resource<AsmaAlHusnaApi>>()

        asmaAlHusna.value = dummyAsmaAlHusna
        `when`(repository.fetchAsmaAlHusna()).thenReturn(asmaAlHusna)
        viewModel.asmaAlHusnaApi.observeForever(observer)

        verify(observer).onChanged(dummyAsmaAlHusna)
    } */

}