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
import com.programmergabut.solatkuy.ui.main.fragmentquran.QuranFragmentViewModel
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
        verify(fakeQuranRepository).observeListFavSurah()
    }

    @Test
    fun `fetchAllSurah, observe allSurah`() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val observer = mock<Observer<Resource<List<Data>?>>>()
        val dummySelectedSurahAr = Resource.success(DummyRetValueTest.fetchAllSurahAr<QuranRepositoryImplTest>())
        dummySelectedSurahAr.data?.statusResponse = "1"
        val dummySelectedSurahData = Resource.success(DummyRetValueTest.fetchAllSurahWithLowerCase<QuranFragmentViewModelTest>())

        `when`(fakeQuranRepository.fetchAllSurah()).thenReturn(dummySelectedSurahAr.data!!.toDeferred())

        viewModel.allSurah.observeForever(observer)

        viewModel.fetchAllSurah()
        val result = viewModel.allSurah.value

        verify(fakeQuranRepository).fetchAllSurah().toDeferred()
        assertEquals(dummySelectedSurahData, result)
        verify(observer).onChanged(dummySelectedSurahData)

        viewModel.allSurah.removeObserver(observer)
    }

    @Test
    fun `getSurahBySeachString, observe allSurah that contains searched string`() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val searchString = "Al Faatiha"
        val observer = mock<Observer<Resource<List<Data>?>>>()
        val dummySelectedSurahAr = Resource.success(DummyRetValueTest.fetchAllSurahAr<QuranRepositoryImplTest>())
        dummySelectedSurahAr.data?.statusResponse = "1"
        val dummySelectedSurahData = Resource.success(DummyRetValueTest.fetchAllSurahWithLowerCase<QuranFragmentViewModelTest>())
        val filteredData = dummySelectedSurahData.data?.filter { data ->
            data.englishNameLowerCase == searchString.toLowerCase()
        }

        `when`(fakeQuranRepository.fetchAllSurah()).thenReturn(dummySelectedSurahAr.data!!.toDeferred())

        viewModel.allSurah.observeForever(observer)

        viewModel.fetchAllSurah()
        viewModel.getSurahBySeachString(searchString)
        val result = viewModel.allSurah.value

        assertEquals(filteredData, result?.data)
        verify(observer).onChanged(dummySelectedSurahData)

        viewModel.allSurah.removeObserver(observer)
    }

    @Test
    fun `getSurahByJuzz, observe allSurah based on Al-Quran juzz`() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val observer = mock<Observer<Resource<List<Data>?>>>()
        val dummySelectedSurahAr = Resource.success(DummyRetValueTest.fetchAllSurahAr<QuranRepositoryImplTest>())
        dummySelectedSurahAr.data?.statusResponse = "1"
        val dummySelectedSurahData = Resource.success(DummyRetValueTest.fetchAllSurahWithLowerCase<QuranFragmentViewModelTest>())
        val filteredData = dummySelectedSurahData.data?.filter { data ->
            data.number in 1..2
        }

        `when`(fakeQuranRepository.fetchAllSurah()).thenReturn(dummySelectedSurahAr.data!!.toDeferred())

        viewModel.allSurah.observeForever(observer)

        viewModel.fetchAllSurah()
        viewModel.getSurahByJuzz(1)
        val result = viewModel.allSurah.value

        assertEquals(filteredData, result?.data)
        verify(observer).onChanged(dummySelectedSurahData)

        viewModel.allSurah.removeObserver(observer)
    }

}