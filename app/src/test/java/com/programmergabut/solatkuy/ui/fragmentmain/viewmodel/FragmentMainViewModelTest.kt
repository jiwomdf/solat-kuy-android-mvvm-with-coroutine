package com.programmergabut.solatkuy.ui.fragmentmain.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.ui.fragmentmain.FragmentMainViewModel
import com.programmergabut.solatkuy.ui.fragmentsetting.FragmentSettingViewModel
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.generator.DummyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FragmentMainViewModelTest {

    private lateinit var viewModel: FragmentMainViewModel

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
        viewModel = FragmentMainViewModel(repository)

        viewModel.fetchNotifiedPrayer(msApi1)
        viewModel.fetchQuranSurah(1)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun syncNotifiedPrayer() = runBlockingTest{
        val observer = mock<Observer<Resource<List<NotifiedPrayer>>>>()
        val dummyNotifiedPrayer = Resource.success(DummyData.getNotifiedPrayer())

        /* val notifiedPrayer = MutableLiveData<Resource<List<NotifiedPrayer>>>()
        notifiedPrayer.value = dummyNotifiedPrayer */

        `when`(repository.syncNotifiedPrayer(msApi1)).thenReturn(dummyNotifiedPrayer.data)

        viewModel.notifiedPrayer.observeForever(observer)

        verify(observer).onChanged(dummyNotifiedPrayer)
    }

    @Test
    fun fetchReadSurahEn() = runBlockingTest{
        val observer = mock<Observer<Resource<ReadSurahEnResponse>>>()
        val dummyQuranSurah = Resource.success(DummyData.fetchSurahApi())

        /* val quranSurah = MutableLiveData<Resource<ReadSurahEnResponse>>()
        quranSurah.value = dummyQuranSurah */

        `when`(repository.fetchReadSurahEn(1)).thenReturn(dummyQuranSurah.data)

        viewModel.readSurahEn.observeForever(observer)

        verify(observer).onChanged(dummyQuranSurah)
    }

}