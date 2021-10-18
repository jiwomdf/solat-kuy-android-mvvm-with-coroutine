package com.programmergabut.solatkuy.ui.main.quran.listsurah

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.nhaarman.mockitokotlin2.mock
import com.programmergabut.solatkuy.CoroutineTestUtil.Companion.toDeferred
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.data.FakeQuranRepository
import com.programmergabut.solatkuy.data.QuranRepositoryImplTest
import com.programmergabut.solatkuy.data.local.localentity.MsSurah
import com.programmergabut.solatkuy.data.remote.json.quranallsurahJson.Result
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.SharedPrefUtil
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
class ListMsSurahFragmentViewModelTest {

    private lateinit var viewModel: ListSurahViewModel
    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()
    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()
    @Mock
    private lateinit var fakeQuranRepository: FakeQuranRepository
    @Mock
    private lateinit var sharedPrefUtil: SharedPrefUtil

    @Before
    fun setUp(){
        viewModel = ListSurahViewModel(fakeQuranRepository,sharedPrefUtil)
        verify(fakeQuranRepository).observeListFavSurah()
    }

    @Test
    fun `fetchAllSurah, observe allSurah`() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val observer = mock<Observer<Resource<List<MsSurah>?>>>()
        val dummySelectedSurahAr = liveData { emit(Resource.success(DummyRetValueTest.getAllSurahAr<QuranRepositoryImplTest>())) }
        val dummySelectedSurahData = Resource.success(DummyRetValueTest.fetchAllSurahWithLowerCase<ListMsSurahFragmentViewModelTest>())

        `when`(fakeQuranRepository.getAllSurah()).thenReturn(dummySelectedSurahAr)

        viewModel.allSurah.observeForever(observer)

        viewModel.getAllSurah()
        val result = viewModel.allSurah.value

        verify(fakeQuranRepository).getAllSurah().toDeferred()
        assertEquals(dummySelectedSurahData, result)
        verify(observer).onChanged(dummySelectedSurahData)

        viewModel.allSurah.removeObserver(observer)
    }

    /* @Test
    fun `getSurahBySeachString, observe allSurah that contains searched string`() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val searchString = "Al Faatiha"
        val observer = mock<Observer<Resource<List<Result>?>>>()
        val dummySelectedSurahAr = Resource.success(DummyRetValueTest.fetchAllSurahAr<QuranRepositoryImplTest>())
        dummySelectedSurahAr.data?.status = "1"
        val dummySelectedSurahData = Resource.success(DummyRetValueTest.fetchAllSurahWithLowerCase<ListMsSurahFragmentViewModelTest>())
        val filteredData = dummySelectedSurahData.data?.filter { data ->
            data.englishNameLowerCase == searchString.toLowerCase()
        }

        `when`(fakeQuranRepository.fetchAllSurah()).thenReturn(dummySelectedSurahAr.data!!.toDeferred())

        viewModel.allSurah.observeForever(observer)

        viewModel.getAllSurah()
        viewModel.getSurahBySeachString(searchString)
        val result = viewModel.allSurah.value

        assertEquals(filteredData, result?.data)
        verify(observer).onChanged(dummySelectedSurahData)

        viewModel.allSurah.removeObserver(observer)
    } */

    /* @Test
    fun `getSurahByJuzz, observe allSurah based on Al-Quran juzz`() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val observer = mock<Observer<Resource<List<Result>?>>>()
        val dummySelectedSurahAr = Resource.success(DummyRetValueTest.fetchAllSurahAr<QuranRepositoryImplTest>())
        dummySelectedSurahAr.data?.status = "1"
        val dummySelectedSurahData = Resource.success(DummyRetValueTest.fetchAllSurahWithLowerCase<ListMsSurahFragmentViewModelTest>())
        val filteredData = dummySelectedSurahData.data?.filter { data ->
            data.number in 1..2
        }

        `when`(fakeQuranRepository.fetchAllSurah()).thenReturn(dummySelectedSurahAr.data!!.toDeferred())

        viewModel.allSurah.observeForever(observer)

        viewModel.getAllSurah()
        viewModel.getSurahByJuzz(1)
        val result = viewModel.allSurah.value

        assertEquals(filteredData, result?.data)
        verify(observer).onChanged(dummySelectedSurahData)

        viewModel.allSurah.removeObserver(observer)
    } */

}