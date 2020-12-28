package com.programmergabut.solatkuy.ui.activityreadsurah

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyArgument
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.data.FakeQuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
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
class ReadSurahActivityTest{

    private lateinit var viewModel: ReadSurahViewModel

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var fakeQuranRepository: FakeQuranRepository

    private val surahID = DummyArgument.surahID
    private val msFavAyah = DummyArgument.msFavAyah
    private val msFavSurah = DummyArgument.msFavSurah

    @Before
    fun before(){
        viewModel = ReadSurahViewModel(fakeQuranRepository)
    }

    @Test
    fun fetchQuranSurah() = coroutinesTestRule.testDispatcher.runBlockingTest{

        //given
        val observer = mock<Observer<Resource<ReadSurahArResponse>>>()

        val dummyDataAr = DummyRetValueTest.surahArID_1()
        dummyDataAr.statusResponse = "1"
        val dummySelectedSurahAr = Resource.success(dummyDataAr)

        val dummyDataEn = DummyRetValueTest.surahEnID_1()
        dummyDataEn.statusResponse = "1"
        val dummySelectedSurahEn = Resource.success(dummyDataEn)

        viewModel.fetchedArSurah = DummyRetValueTest.surahArID_1()

        //scenario
        Mockito.`when`(fakeQuranRepository.fetchReadSurahAr(surahID)).thenReturn(dummySelectedSurahAr.data!!.toDeferred())
        Mockito.`when`(fakeQuranRepository.fetchReadSurahEn(surahID)).thenReturn(dummySelectedSurahEn.data!!.toDeferred())
        //start observer
        viewModel.selectedSurahAr.observeForever(observer)

        //when
        viewModel.fetchReadSurahAr(surahID)
        val result = viewModel.selectedSurahAr.value

        //--verify
        Mockito.verify(fakeQuranRepository).fetchReadSurahAr(surahID).toDeferred()
        Assert.assertEquals(dummySelectedSurahAr, result)
        Mockito.verify(observer).onChanged(dummySelectedSurahAr)

        //end observer
        viewModel.selectedSurahAr.removeObserver(observer)
    }

    @Test
    fun getFavSurahBySurahID() = coroutinesTestRule.testDispatcher.runBlockingTest{

        //given
        val observer = mock<Observer<MsFavSurah>>()
        val dummyLiveData: MutableLiveData<MsFavSurah> = MutableLiveData()
        dummyLiveData.value = DummyRetValueTest.getFavSurahBySurahID(surahID)[0]

        //scenario
        Mockito.`when`(fakeQuranRepository.getFavSurahBySurahID(surahID)).thenReturn(dummyLiveData)

        //start observer
        viewModel.msFavSurah.observeForever(observer)

        //when
        viewModel.getFavSurahBySurahID(surahID)
        val result = viewModel.msFavSurah.value

        //--verify
        Mockito.verify(fakeQuranRepository).getFavSurahBySurahID(surahID)
        Assert.assertEquals(dummyLiveData.value, result)
        Mockito.verify(observer).onChanged(dummyLiveData.value)

        //end observer
        viewModel.msFavSurah.removeObserver(observer)
    }

    @Test
    fun getListFavAyahBySurahID() = coroutinesTestRule.testDispatcher.runBlockingTest{

        //given
        val observer = mock<Observer<Resource<List<MsFavAyah>>>>()
        val dummyLiveData: MutableLiveData<List<MsFavAyah>> = MutableLiveData()
        dummyLiveData.value = DummyRetValueTest.getListMsFavAyah()
        viewModel.fetchedArSurah = DummyRetValueTest.surahArID_1()

        //scenario
        Mockito.`when`(fakeQuranRepository.getListFavAyahBySurahID(surahID)).thenReturn(dummyLiveData)

        //start observer
        viewModel.msFavAyahBySurahID.observeForever(observer)

        //when
        viewModel.getListFavAyahBySurahID(surahID, 0, 0 ,0)
        val result = viewModel.msFavAyahBySurahID.value

        //--verify
        Mockito.verify(fakeQuranRepository).getListFavAyahBySurahID(surahID)
        Assert.assertEquals(dummyLiveData.value, result?.data)
        Mockito.verify(observer).onChanged(Resource.success(dummyLiveData.value))

        //end observer
        viewModel.msFavAyahBySurahID.removeObserver(observer)
    }

    @Test
    fun insertFavAyah() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.insertFavAyah(msFavAyah)
        verify(fakeQuranRepository).insertFavAyah(msFavAyah)
    }
    @Test
    fun deleteFavAyah() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.deleteFavAyah(msFavAyah)
        verify(fakeQuranRepository).deleteFavAyah(msFavAyah)
    }
    @Test
    fun insertFavSurah() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.insertFavSurah(msFavSurah)
        verify(fakeQuranRepository).insertFavSurah(msFavSurah)
    }
    @Test
    fun deleteFavSurah() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.deleteFavSurah(msFavSurah)
        verify(fakeQuranRepository).deleteFavSurah(msFavSurah)
    }

    fun <T> T.toDeferred() = CompletableDeferred(this)

}