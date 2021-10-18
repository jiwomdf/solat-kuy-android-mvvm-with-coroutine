package com.programmergabut.solatkuy.ui.main.quran.readsurah

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.data.FakeQuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.util.SharedPrefUtil
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ReadMsSurahFragmentViewModelTest {

    private lateinit var viewModel: ReadSurahViewModel

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var fakeQuranRepository: FakeQuranRepository

    @Mock
    private lateinit var sharedPrefUtil: SharedPrefUtil
    private val surahID = DummyRetValueTest.surahID
    private val msFavSurah = DummyRetValueTest.msFavSurah

    @Before
    fun before() {
        viewModel = ReadSurahViewModel(fakeQuranRepository, sharedPrefUtil)
    }

    @Test
    fun `getFavSurahBySurahID, observe favSurahBySurahID`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            val observer = mock<Observer<MsFavSurah?>>()
            val dummyData = MutableLiveData(DummyRetValueTest.msFavSurah)

            Mockito.`when`(fakeQuranRepository.observeFavSurahBySurahID(surahID))
                .thenReturn(dummyData)

            viewModel.favSurahBySurahID.observeForever(observer)

            viewModel.getSelectedSurah(surahID)
            val result = viewModel.favSurahBySurahID.value

            Mockito.verify(fakeQuranRepository).observeFavSurahBySurahID(surahID)
            Assert.assertEquals(dummyData.value, result)
            Mockito.verify(observer).onChanged(dummyData.value)

            viewModel.favSurahBySurahID.removeObserver(observer)
        }

    @Test
    fun `insertFavSurah, insertFavSurah called`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.insertFavSurah(msFavSurah)
            verify(fakeQuranRepository).insertFavSurah(msFavSurah)
        }

    @Test
    fun `deleteFavSurah, deleteFavSurah called`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.deleteFavSurah(msFavSurah)
            verify(fakeQuranRepository).deleteFavSurah(msFavSurah)
        }

    fun <T> T.toDeferred() = CompletableDeferred(this)

}