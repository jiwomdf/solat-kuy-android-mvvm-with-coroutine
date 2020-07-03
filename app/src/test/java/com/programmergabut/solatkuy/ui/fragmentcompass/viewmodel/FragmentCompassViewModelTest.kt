package com.programmergabut.solatkuy.ui.fragmentcompass.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyRetValue
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.ui.fragmentcompass.FragmentCompassViewModel
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
class FragmentCompassViewModelTest {

    private lateinit var viewModel: FragmentCompassViewModel

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var repository: Repository

    private val msApi1 = MsApi1(0, "", "", "","","")

    @Before
    fun before(){
        viewModel = FragmentCompassViewModel(repository)

        Mockito.verify(repository).getMsApi1()
    }

    @Test
    fun fetchCompassApi() = coroutinesTestRule.testDispatcher.runBlockingTest{

        //given
        val observer = mock<Observer<Resource<CompassResponse>>>()
        val dummyCompass = Resource.success(DummyRetValue.fetchCompassApi())
        `when`(repository.fetchCompass(msApi1)).thenReturn(dummyCompass.data)

        //when
        viewModel.fetchCompassApi(msApi1)
        val result = viewModel.compass.value

        //--return value
        Mockito.verify(repository).fetchCompass(msApi1)
        Assert.assertEquals(dummyCompass, result)

        //--observer
        viewModel.compass.observeForever(observer)
        Mockito.verify(observer).onChanged(dummyCompass)
    }
}