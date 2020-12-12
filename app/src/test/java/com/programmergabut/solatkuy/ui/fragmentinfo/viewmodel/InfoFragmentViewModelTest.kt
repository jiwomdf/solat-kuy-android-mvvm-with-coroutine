package com.programmergabut.solatkuy.ui.fragmentinfo.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyArgument
import com.programmergabut.solatkuy.DummyRetValue
import com.programmergabut.solatkuy.data.PrayerRepositoryImpl
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
class InfoFragmentViewModelTest {

    private lateinit var viewModel: FragmentInfoViewModel

    @get:Rule
    val instantExecutor: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var prayerRepositoryImpl: PrayerRepositoryImpl

    private val msApi1 = DummyArgument.msApi1

    @Before
    fun before(){
        viewModel = FragmentInfoViewModel(prayerRepositoryImpl)

        Mockito.verify(prayerRepositoryImpl).getMsApi1()
    }

    @Test
    fun fetchPrayerApi() = coroutinesTestRule.testDispatcher.runBlockingTest{

        //given
        val dummyPrayerApi = Resource.success(DummyRetValue.fetchPrayerApi())
        `when`(prayerRepositoryImpl.fetchPrayerApi(msApi1)).thenReturn(dummyPrayerApi.data)

        //when
        viewModel.fetchPrayerApi(msApi1)
        val result = viewModel.prayer

        //--return value
        Mockito.verify(prayerRepositoryImpl).fetchPrayerApi(msApi1)
        Assert.assertEquals(dummyPrayerApi.data, result)

        //--observer
        /* viewModel.prayerStatus.observeForever(observerStatus)
        Mockito.verify(observer).onChanged(dummyPrayerApi) */
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