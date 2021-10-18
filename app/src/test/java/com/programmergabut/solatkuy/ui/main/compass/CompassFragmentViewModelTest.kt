package com.programmergabut.solatkuy.ui.main.compass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.programmergabut.solatkuy.CoroutineTestUtil.Companion.toDeferred
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.data.FakePrayerRepository
import com.programmergabut.solatkuy.data.remote.json.compassJson.CompassResponse
import com.programmergabut.solatkuy.ui.main.qibla.CompassViewModel
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.SharedPrefUtil
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

    private lateinit var viewModel: CompassViewModel
    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()
    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()
    @Mock
    private lateinit var prayerRepository: FakePrayerRepository
    @Mock
    private lateinit var sharedPrefUtil: SharedPrefUtil
    private val msApi1 = DummyRetValueTest.msApi1

    @Before
    fun before(){
        viewModel = CompassViewModel(prayerRepository,sharedPrefUtil)
        Mockito.verify(prayerRepository).observeMsApi1()
    }

    @Test
    fun `fetchCompassApi, observe compass change`() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val observer = mock<Observer<Resource<CompassResponse>>>()
        val dummyCompass = Resource.success(DummyRetValueTest.fetchCompassApi<CompassFragmentViewModelTest>())
        dummyCompass.data?.responseStatus = "1"
        `when`(prayerRepository.fetchQibla(msApi1)).thenReturn(dummyCompass.data!!.toDeferred())

        viewModel.fetchCompassApi(msApi1)
        val result = viewModel.compass.value

        Mockito.verify(prayerRepository).fetchQibla(msApi1).toDeferred()
        Assert.assertEquals(dummyCompass, result)

        viewModel.compass.observeForever(observer)
        Mockito.verify(observer).onChanged(dummyCompass)
    }
}