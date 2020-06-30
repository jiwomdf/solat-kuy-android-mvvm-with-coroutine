package com.programmergabut.solatkuy.ui.fragmentcompass.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.ui.fragmentcompass.FragmentCompassViewModel
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.generator.DummyData
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FragmentCompassViewModelTest {

    private lateinit var viewModel: FragmentCompassViewModel

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository

    @Mock
    private lateinit var context: Application

    private val msApi1 = MsApi1(0, "", "", "","","")

    @Before
    fun setUp() {
        viewModel =
            FragmentCompassViewModel(
                context,
                repository
            )

        //invoke fetchCompassApi
        viewModel.fetchCompassApi(msApi1)
    }

    @Test
    fun fetchCompassApi() {

        val observer = mock<Observer<Resource<CompassResponse>>>()
        val dummyCompass = Resource.success(DummyData.fetchCompassApi())
        val compass = MutableLiveData<Resource<CompassResponse>>()

        compass.value = dummyCompass
        `when`(repository.fetchCompass(msApi1)).thenReturn(compass)
        viewModel.compassResponse.observeForever(observer)

        verify(observer).onChanged(dummyCompass)
    }
}