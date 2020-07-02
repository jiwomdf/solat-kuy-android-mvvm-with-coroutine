package com.programmergabut.solatkuy.ui.fragmentmain.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyArgument
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.ui.fragmentmain.FragmentMainViewModel
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.DummyRetValue
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FragmentMainViewModelTest {

    private lateinit var viewModel: FragmentMainViewModel

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var repository: Repository

    private val msApi1 = DummyArgument.msApi1
    private val surahID = DummyArgument.surahID


    @Before
    fun before(){
        viewModel = FragmentMainViewModel(repository)
    }

    @Test
    fun syncNotifiedPrayer() = coroutinesTestRule.testDispatcher.runBlockingTest{
        //given
        val observer = mock<Observer<Resource<List<NotifiedPrayer>>>>()
        val dummyNotifiedPrayer = Resource.success(DummyRetValue.getNotifiedPrayer())
        `when`(repository.syncNotifiedPrayer(msApi1)).thenReturn(dummyNotifiedPrayer.data)

        //when
        viewModel.syncNotifiedPrayer(msApi1)
        val result = viewModel.notifiedPrayer.value

        //--return value
        verify(repository).syncNotifiedPrayer(msApi1)
        assertEquals(dummyNotifiedPrayer, result)

        //--observer
        viewModel.notifiedPrayer.observeForever(observer)
        verify(observer).onChanged(dummyNotifiedPrayer)
    }

    @Test
    fun fetchReadSurahEn() = coroutinesTestRule.testDispatcher.runBlockingTest{
        //given
        val observer = mock<Observer<Resource<ReadSurahEnResponse>>>()
        val dummyQuranSurah = Resource.success(DummyRetValue.surahEnID_1())
        `when`(repository.fetchReadSurahEn(surahID)).thenReturn(dummyQuranSurah.data)

        //when
        viewModel.fetchQuranSurah(surahID)
        val result = viewModel.readSurahEn.value

        //--return value
        verify(repository).fetchReadSurahEn(surahID)
        assertEquals(dummyQuranSurah, result)

        //--observer
        viewModel.readSurahEn.observeForever(observer)
        verify(observer).onChanged(dummyQuranSurah)
    }

}