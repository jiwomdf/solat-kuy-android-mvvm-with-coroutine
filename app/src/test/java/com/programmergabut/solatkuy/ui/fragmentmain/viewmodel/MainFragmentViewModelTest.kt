package com.programmergabut.solatkuy.ui.fragmentmain.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.programmergabut.solatkuy.CoroutineTestUtil.Companion.toDeferred
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyArgument
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.ui.activitymain.fragmentmain.FragmentMainViewModel
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.data.FakePrayerRepository
import com.programmergabut.solatkuy.data.FakeQuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
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
class MainFragmentViewModelTest {

    private lateinit var viewModel: FragmentMainViewModel

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var fakePrayerRepository: FakePrayerRepository

    @Mock
    private lateinit var fakeQuranRepository: FakeQuranRepository

    private val msApi1 = DummyArgument.msApi1
    private val surahID = DummyArgument.surahID
    private val mapPrayer = DummyArgument.getMapPrayer()

    @Before
    fun before(){
        viewModel = FragmentMainViewModel(fakePrayerRepository, fakeQuranRepository)

        verify(fakePrayerRepository).getMsApi1()
    }

    @Test
    fun syncNotifiedPrayer() = coroutinesTestRule.testDispatcher.runBlockingTest{
        //given
        val observer = mock<Observer<Resource<List<NotifiedPrayer>>>>()
        val dummyNotifiedPrayer = Resource.success(DummyRetValueTest.fetchPrayerApi<MainFragmentViewModelTest>())
        val dummyPrayerResponse = Resource.success(DummyRetValueTest.getNotifiedPrayer())
        dummyNotifiedPrayer.data?.statusResponse = "1"
        `when`(fakePrayerRepository.fetchPrayerApi(msApi1)).thenReturn(dummyNotifiedPrayer.data!!.toDeferred())
        `when`(fakePrayerRepository.getListNotifiedPrayerSync()).thenReturn(dummyPrayerResponse.data)

        //when
        viewModel.syncNotifiedPrayer(msApi1)
        val result = viewModel.notifiedPrayer.value

        //--return value
        verify(fakePrayerRepository).fetchPrayerApi(msApi1).toDeferred()
        assertEquals(dummyPrayerResponse, result)

        //--observer
        viewModel.notifiedPrayer.observeForever(observer)
        verify(observer).onChanged(dummyPrayerResponse)
    }

    @Test
    fun fetchReadSurahEn() = coroutinesTestRule.testDispatcher.runBlockingTest{
        //given
        val observer = mock<Observer<Resource<ReadSurahEnResponse>>>()
        val dummyQuranSurah = Resource.success(DummyRetValueTest.surahEnID_1<MainFragmentViewModelTest>())
        dummyQuranSurah.data?.statusResponse= "1"
        `when`(fakeQuranRepository.fetchReadSurahEn(surahID)).thenReturn(dummyQuranSurah.data!!.toDeferred())

        //when
        viewModel.fetchReadSurahEn(surahID)
        val result = viewModel.readSurahEn.value

        //--return value
        verify(fakeQuranRepository).fetchReadSurahEn(surahID).toDeferred()
        assertEquals(dummyQuranSurah, result)

        //--observer
        viewModel.readSurahEn.observeForever(observer)
        verify(observer).onChanged(dummyQuranSurah)
    }

    @Test
    fun getMsSetting() = coroutinesTestRule.testDispatcher.runBlockingTest {

        //given
        val observer = mock<Observer<Resource<MsSetting>>>()
        val dummyLiveData: MutableLiveData<MsSetting> = MutableLiveData()
        dummyLiveData.value = DummyRetValueTest.getMsSetting()

        //scenario
        `when`(fakePrayerRepository.getMsSetting()).thenReturn(dummyLiveData)

        //start observer
        viewModel.msSetting.observeForever(observer)

        //when
        viewModel.getMsSetting()
        val result = viewModel.msSetting.value

        //--verify
        verify(fakePrayerRepository).getMsSetting()
        assertEquals(dummyLiveData.value, result?.data)
        verify(observer).onChanged(Resource.success(dummyLiveData.value))

        //end observer
        viewModel.msSetting.removeObserver(observer)
    }


    @Test
    fun updateMsApi1() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.updateMsApi1(msApi1)
        com.nhaarman.mockitokotlin2.verify(fakePrayerRepository).updateMsApi1(msApi1)
    }

    @Test
    fun updatePrayerIsNotified() = coroutinesTestRule.testDispatcher.runBlockingTest {

        viewModel.updatePrayerIsNotified(mapPrayer.keys.elementAt(0), true)
        com.nhaarman.mockitokotlin2.verify(fakePrayerRepository).updatePrayerIsNotified(mapPrayer.keys.elementAt(0), true)
    }

    @Test
    fun updateIsUsingDBQuotes() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.updateIsUsingDBQuotes(true)
        com.nhaarman.mockitokotlin2.verify(fakePrayerRepository).updateIsUsingDBQuotes(true)
    }

}