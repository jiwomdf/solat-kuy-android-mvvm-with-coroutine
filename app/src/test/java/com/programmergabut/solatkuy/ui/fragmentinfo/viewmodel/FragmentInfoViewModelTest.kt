package com.programmergabut.solatkuy.ui.fragmentinfo.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyArgument
import com.programmergabut.solatkuy.DummyRetValue
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.ui.fragmentinfo.FragmentInfoViewModel
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FragmentInfoViewModelTest {

    private lateinit var viewModel: FragmentInfoViewModel

    @get:Rule
    val instantExecutor: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var prayerRepository: PrayerRepository

    private val msApi1 = DummyArgument.msApi1

    @Before
    fun before(){
        viewModel = FragmentInfoViewModel(prayerRepository)

        Mockito.verify(prayerRepository).getMsApi1()
    }

    @Test
    fun fetchPrayerApi() = coroutinesTestRule.testDispatcher.runBlockingTest{

        //given
        val observer = mock<Observer<Resource<PrayerResponse>>>()
        val dummyPrayerApi = Resource.success(DummyRetValue.fetchPrayerApi())
        `when`(prayerRepository.fetchPrayerApi(msApi1)).thenReturn(dummyPrayerApi.data)

        //when
        viewModel.fetchPrayerApi(msApi1)
        val result = viewModel.prayer.value

        //--return value
        Mockito.verify(prayerRepository).fetchPrayerApi(msApi1)
        Assert.assertEquals(dummyPrayerApi, result)

        //--observer
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