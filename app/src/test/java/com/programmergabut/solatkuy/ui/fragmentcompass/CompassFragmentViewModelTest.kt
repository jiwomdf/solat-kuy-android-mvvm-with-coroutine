package com.programmergabut.solatkuy.ui.fragmentcompass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.programmergabut.solatkuy.CoroutineTestUtil.Companion.toDeferred
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.data.FakePrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.ui.main.fragmentcompass.FragmentCompassViewModel
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CompassFragmentViewModelTest {

    private lateinit var viewModel: FragmentCompassViewModel
    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()
    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()
    @Mock
    private lateinit var prayerRepository: FakePrayerRepository
    private val msApi1 = DummyRetValueTest.msApi1

    @Before
    fun before(){
        viewModel = FragmentCompassViewModel(prayerRepository)
        Mockito.verify(prayerRepository).observeMsApi1()
    }

    @Test
    fun `fetchCompassApi, observe compass change`() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val observer = mock<Observer<Resource<CompassResponse>>>()
        val dummyCompass = Resource.success(DummyRetValueTest.fetchCompassApi<CompassFragmentViewModelTest>())
        dummyCompass.data?.statusResponse = "1"
        `when`(prayerRepository.fetchCompass(msApi1)).thenReturn(dummyCompass.data!!.toDeferred())

        viewModel.fetchCompassApi(msApi1)
        val result = viewModel.compass.value

        Mockito.verify(prayerRepository).fetchCompass(msApi1).toDeferred()
        Assert.assertEquals(dummyCompass, result)

        viewModel.compass.observeForever(observer)
        Mockito.verify(observer).onChanged(dummyCompass)
    }
}