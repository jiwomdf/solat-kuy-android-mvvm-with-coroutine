package com.programmergabut.solatkuy.ui.fragmentcompass.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.ui.fragmentcompass.FragmentCompassViewModel
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.generator.DummyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FragmentCompassViewModelTest {

    private lateinit var viewModel: FragmentCompassViewModel

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository

    private val msApi1 = MsApi1(0, "", "", "","","")

    @ExperimentalCoroutinesApi
    val dispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = FragmentCompassViewModel(repository)

        //invoke fetchCompassApi
        viewModel.fetchCompassApi(msApi1)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchCompassApi() = runBlockingTest{

        val observer = mock<Observer<Resource<CompassResponse>>>()
        val dummyCompass = Resource.success(DummyData.fetchCompassApi())

        /* val compass = MutableLiveData<Resource<CompassResponse>>()
        compass.value = dummyCompass */

        `when`(repository.fetchCompass(msApi1)).thenReturn(dummyCompass.data)
        viewModel.compass.observeForever(observer)

        verify(observer).onChanged(dummyCompass)
    }
}