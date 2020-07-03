package com.programmergabut.solatkuy.ui.fragmentquran

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyRetValue
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.ui.fragmentsetting.FragmentSettingViewModel
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class QuranFragmentViewModelTest {

    private lateinit var viewModel: QuranFragmentViewModel

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var repository: Repository

    @Before
    fun setUp(){
        viewModel = QuranFragmentViewModel(repository)

        Mockito.verify(repository).getListFavSurah()
    }


    @Test
    fun fetchAllSurah() = coroutinesTestRule.testDispatcher.runBlockingTest{

        //given
        val observer = mock<Observer<Resource<AllSurahResponse>>>()
        val dummySelectedSurahAr = Resource.success(DummyRetValue.fetchAllSurah())

        //scenario
        Mockito.`when`(repository.fetchAllSurah()).thenReturn(dummySelectedSurahAr.data)

        //start observer
        viewModel.allSurah.observeForever(observer)

        //when
        viewModel.fetchAllSurah()
        val result = viewModel.allSurah.value

        //--verify
        Mockito.verify(repository).fetchAllSurah()
        assertEquals(dummySelectedSurahAr, result)
        Mockito.verify(observer).onChanged(dummySelectedSurahAr)

        //end observer
        viewModel.allSurah.removeObserver(observer)
    }

}