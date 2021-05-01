package com.programmergabut.solatkuy.ui.main.quran.readsurah

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.data.FakeQuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.util.Resource
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
class ReadMsSurahFragmentViewModelTest{

    private lateinit var viewModel: ReadSurahViewModel
    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()
    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()
    @Mock
    private lateinit var fakeQuranRepository: FakeQuranRepository
    private val surahID = DummyRetValueTest.surahID
    private val msFavAyah = DummyRetValueTest.msFavAyah
    private val msFavSurah = DummyRetValueTest.msFavSurah

    @Before
    fun before(){
        viewModel = ReadSurahViewModel(fakeQuranRepository)
    }

    @Test
    fun `fetchReadSurahAr, observe selectedSurahAr`() =
        coroutinesTestRule.testDispatcher.runBlockingTest{
        val observer = mock<Observer<Resource<ReadSurahArResponse>>>()

        val dummyDataAr = DummyRetValueTest.surahArID_1<ReadMsSurahFragmentViewModelTest>()
        dummyDataAr.statusResponse = "1"
        val dummySelectedSurahAr = Resource.success(dummyDataAr)

        val dummyDataEn = DummyRetValueTest.surahEnID_1<ReadMsSurahFragmentViewModelTest>()
        dummyDataEn.statusResponse = "1"
        val dummySelectedSurahEn = Resource.success(dummyDataEn)

        Mockito.`when`(fakeQuranRepository.fetchReadSurahAr(surahID)).thenReturn(dummySelectedSurahAr.data!!.toDeferred())
        Mockito.`when`(fakeQuranRepository.fetchReadSurahEn(surahID)).thenReturn(dummySelectedSurahEn.data!!.toDeferred())

        viewModel.selectedSurah.observeForever(observer)

        viewModel.fetchReadSurahAr(surahID)
        val result = viewModel.selectedSurah.value

        Mockito.verify(fakeQuranRepository).fetchReadSurahAr(surahID).toDeferred()
        Assert.assertEquals(dummySelectedSurahAr, result)
        Mockito.verify(observer).onChanged(dummySelectedSurahAr)

        viewModel.selectedSurah.removeObserver(observer)
    }

    @Test
    fun `getFavSurahBySurahID, observe favSurahBySurahID`() =
        coroutinesTestRule.testDispatcher.runBlockingTest{
        val observer = mock<Observer<MsFavSurah?>>()
        val dummyData = MutableLiveData(DummyRetValueTest.msFavSurah)

        Mockito.`when`(fakeQuranRepository.observeFavSurahBySurahID(surahID)).thenReturn(dummyData)

        viewModel.favSurahBySurahID.observeForever(observer)

        viewModel.getSelectedSurah(surahID)
        val result = viewModel.favSurahBySurahID.value

        Mockito.verify(fakeQuranRepository).observeFavSurahBySurahID(surahID)
        Assert.assertEquals(dummyData.value, result)
        Mockito.verify(observer).onChanged(dummyData.value)

        viewModel.favSurahBySurahID.removeObserver(observer)
    }

    @Test
    fun `getListFavAyahBySurahID, observe msFavAyahBySurahID`() =
        coroutinesTestRule.testDispatcher.runBlockingTest{
        val observer = mock<Observer<Resource<List<MsFavAyah>>>>()
        val dummyData = DummyRetValueTest.getListMsFavAyah()

        Mockito.`when`(fakeQuranRepository.getListFavAyahBySurahID(surahID)).thenReturn(dummyData)

        viewModel.msFavAyahBySurahID.observeForever(observer)

        viewModel.getListFavAyahBySurahID(surahID, 0, 0 ,0)
        val result = viewModel.msFavAyahBySurahID.value

        Mockito.verify(fakeQuranRepository).getListFavAyahBySurahID(surahID)
        Assert.assertEquals(dummyData, result?.data)
        Mockito.verify(observer).onChanged(Resource.success(dummyData))

        viewModel.msFavAyahBySurahID.removeObserver(observer)
    }

    @Test
    fun `insertFavAyah, insertFavAyah called`() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.insertFavAyah(msFavAyah)
        verify(fakeQuranRepository).insertFavAyah(msFavAyah)
    }
    @Test
    fun `deleteFavAyah, deleteFavAyah called`() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.deleteFavAyah(msFavAyah)
        verify(fakeQuranRepository).deleteFavAyah(msFavAyah)
    }
    @Test
    fun `insertFavSurah, insertFavSurah called`() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.insertFavSurah(msFavSurah)
        verify(fakeQuranRepository).insertFavSurah(msFavSurah)
    }
    @Test
    fun `deleteFavSurah, deleteFavSurah called`() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.deleteFavSurah(msFavSurah)
        verify(fakeQuranRepository).deleteFavSurah(msFavSurah)
    }

    fun <T> T.toDeferred() = CompletableDeferred(this)

}