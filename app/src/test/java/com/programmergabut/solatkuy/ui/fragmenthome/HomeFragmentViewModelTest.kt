package com.programmergabut.solatkuy.ui.fragmenthome

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.programmergabut.solatkuy.CoroutineTestUtil.Companion.toDeferred
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.ui.main.fragmenthome.FragmentMainViewModel
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
class HomeFragmentViewModelTest {

    private lateinit var viewModel: FragmentMainViewModel
    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()
    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()
    @Mock
    private lateinit var fakePrayerRepository: FakePrayerRepository
    @Mock
    private lateinit var fakeQuranRepository: FakeQuranRepository
    private val msApi1 = DummyRetValueTest.msApi1
    private val surahID = DummyRetValueTest.surahID
    private val mapPrayer = DummyRetValueTest.getMapPrayer()

    @Before
    fun before(){
        viewModel = FragmentMainViewModel(fakePrayerRepository, fakeQuranRepository)
        verify(fakePrayerRepository).observeMsApi1()
    }

    @Test
    fun `syncNotifiedPrayer, observe notifiedPrayer`() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val observer = mock<Observer<Resource<List<NotifiedPrayer>>>>()
        val dummyNotifiedPrayer = Resource.success(DummyRetValueTest.fetchPrayerApi<HomeFragmentViewModelTest>())
        val dummyPrayerResponse = Resource.success(DummyRetValueTest.getNotifiedPrayer(), "Application Online")
        dummyNotifiedPrayer.data?.statusResponse = "1"
        `when`(fakePrayerRepository.fetchPrayerApi(msApi1)).thenReturn(dummyNotifiedPrayer.data!!.toDeferred())
        `when`(fakePrayerRepository.getListNotifiedPrayer()).thenReturn(dummyPrayerResponse.data)

        viewModel.syncNotifiedPrayer(msApi1)
        val result = viewModel.notifiedPrayer.value

        verify(fakePrayerRepository).fetchPrayerApi(msApi1).toDeferred()
        assertEquals(dummyPrayerResponse, result)

        viewModel.notifiedPrayer.observeForever(observer)
        verify(observer).onChanged(dummyPrayerResponse)
    }

    @Test
    fun `fetchReadSurahEn, observe readSurahEn`() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val observer = mock<Observer<Resource<ReadSurahEnResponse>>>()
        val dummyQuranSurah = Resource.success(DummyRetValueTest.surahEnID_1<HomeFragmentViewModelTest>())
        dummyQuranSurah.data?.statusResponse= "1"
        `when`(fakeQuranRepository.fetchReadSurahEn(surahID)).thenReturn(dummyQuranSurah.data!!.toDeferred())

        viewModel.fetchReadSurahEn(surahID)
        val result = viewModel.readSurahEn.value

        verify(fakeQuranRepository).fetchReadSurahEn(surahID).toDeferred()
        assertEquals(dummyQuranSurah, result)

        viewModel.readSurahEn.observeForever(observer)
        verify(observer).onChanged(dummyQuranSurah)
    }

    @Test
    fun `getMsSetting, observe msSetting`() = coroutinesTestRule.testDispatcher.runBlockingTest {
        val observer = mock<Observer<MsSetting>>()
        val dummyData = MutableLiveData(DummyRetValueTest.msSetting)

        `when`(fakePrayerRepository.observeMsSetting()).thenReturn(dummyData)

        viewModel.msSetting.observeForever(observer)

        viewModel.getMsSetting()
        val result = viewModel.msSetting.value

        verify(fakePrayerRepository).observeMsSetting()
        assertEquals(dummyData.value, result)
        verify(observer).onChanged(dummyData.value)

        viewModel.msSetting.removeObserver(observer)
    }


    @Test
    fun `updateMsApi1, updateMsApi1() called`() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.updateMsApi1(msApi1)
        verify(fakePrayerRepository).updateMsApi1(msApi1)
    }

    @Test
    fun `updatePrayerIsNotified, updatePrayerIsNotified() called`() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.updatePrayerIsNotified(mapPrayer.keys.elementAt(0), true)
        verify(fakePrayerRepository).updatePrayerIsNotified(mapPrayer.keys.elementAt(0), true)
    }

    @Test
    fun `updateIsUsingDBQuotes, updateIsUsingDBQuotes() called`() = coroutinesTestRule.testDispatcher.runBlockingTest {
        viewModel.updateIsUsingDBQuotes(true)
        verify(fakePrayerRepository).updateIsUsingDBQuotes(true)
    }

}