package com.programmergabut.solatkuy.ui.fragmentquran

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.programmergabut.solatkuy.CoroutineTestUtil.Companion.toDeferred
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.data.FakeQuranRepository
import com.programmergabut.solatkuy.data.QuranRepositoryImplTest
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data
import com.programmergabut.solatkuy.ui.activitymain.fragmentquran.QuranFragmentViewModel
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
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
    private lateinit var fakeQuranRepository: FakeQuranRepository

    @Before
    fun setUp(){
        viewModel = QuranFragmentViewModel(fakeQuranRepository)

        verify(fakeQuranRepository).getListFavSurah()
    }


    @Test
    fun fetchAllSurah() = coroutinesTestRule.testDispatcher.runBlockingTest{

        //given
        val observer = mock<Observer<Resource<List<Data>>>>()
        val dummySelectedSurahAr = Resource.success(DummyRetValueTest.fetchAllSurahAr<QuranRepositoryImplTest>())
        dummySelectedSurahAr.data?.statusResponse = "1"
        val dummySelectedSurahData = Resource.success(DummyRetValueTest.fetchAllSurahWithLowerCase<QuranFragmentViewModelTest>())

        //scenario
        `when`(fakeQuranRepository.fetchAllSurah()).thenReturn(dummySelectedSurahAr.data!!.toDeferred())

        //start observer
        viewModel.allSurah.observeForever(observer)

        //when
        viewModel.fetchAllSurah()
        val result = viewModel.allSurah.value

        //--verify
        verify(fakeQuranRepository).fetchAllSurah().toDeferred()
        assertEquals(dummySelectedSurahData, result)
        verify(observer).onChanged(dummySelectedSurahData)

        //end observer
        viewModel.allSurah.removeObserver(observer)
    }
}