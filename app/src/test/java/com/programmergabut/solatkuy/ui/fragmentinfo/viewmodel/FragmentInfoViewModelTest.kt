package com.programmergabut.solatkuy.ui.fragmentinfo.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.ui.fragmentinfo.FragmentInfoViewModel
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.generator.DummyData
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FragmentInfoViewModelTest {

    private lateinit var viewModel: FragmentInfoViewModel

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
            FragmentInfoViewModel(
                context,
                repository
            )
        viewModel.fetchPrayerApi(msApi1)
    }

    @Test
    fun fetchPrayerApi(){
        val observer = mock<Observer<Resource<PrayerResponse>>>()
        val dummyPrayerApi = Resource.success(DummyData.fetchPrayerApi())

        val prayerApi = MutableLiveData<Resource<PrayerResponse>>()
        prayerApi.value = dummyPrayerApi
        `when`(repository.fetchPrayerApi(msApi1)).thenReturn(prayerApi)

        viewModel.prayerResponse.observeForever(observer)

        Mockito.verify(observer).onChanged(dummyPrayerApi)
    }

   /* @Test
    fun fetchAsmaAlHusna(){
        val observer = mock<Observer<Resource<AsmaAlHusnaApi>>>()
        val dummyAsmaAlHusna = Resource.success(DummyData.fetchAsmaAlHusnaApi())
        val asmaAlHusna = MutableLiveData<Resource<AsmaAlHusnaApi>>()

        asmaAlHusna.value = dummyAsmaAlHusna
        `when`(repository.fetchAsmaAlHusna()).thenReturn(asmaAlHusna)
        viewModel.asmaAlHusnaApi.observeForever(observer)

        verify(observer).onChanged(dummyAsmaAlHusna)
    } */

}