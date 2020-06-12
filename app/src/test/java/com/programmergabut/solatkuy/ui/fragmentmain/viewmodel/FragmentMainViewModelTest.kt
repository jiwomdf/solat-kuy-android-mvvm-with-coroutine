package com.programmergabut.solatkuy.ui.fragmentmain.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnApi
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.generator.DummyData
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class FragmentMainViewModelTest {

    private lateinit var viewModel: FragmentMainViewModel

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository

    @Mock
    private lateinit var context: Application

    private val msApi1 = MsApi1(0, "", "", "","","")

    @Before
    fun setUp() {
        viewModel = FragmentMainViewModel(context, repository)
        viewModel.fetchQuranSurah(1)
        viewModel.fetchPrayerApi(msApi1)
    }

    @Test
    fun getNotifiedPrayer(){
        val observer = mock<Observer<Resource<List<NotifiedPrayer>>>>()
        val dummyNotifiedPrayer = Resource.success(DummyData.getNotifiedPrayer())

        val notifiedPrayer = MutableLiveData<Resource<List<NotifiedPrayer>>>()
        notifiedPrayer.value = dummyNotifiedPrayer
        `when`(repository.syncNotifiedPrayer(msApi1)).thenReturn(notifiedPrayer)

        viewModel.notifiedPrayer.observeForever(observer)

        verify(observer).onChanged(dummyNotifiedPrayer)
    }

    @Test
    fun getQuranSurah(){
        val observer = mock<Observer<Resource<ReadSurahEnApi>>>()
        val dummyQuranSurah = Resource.success(DummyData.fetchSurahApi())
        val quranSurah = MutableLiveData<Resource<ReadSurahEnApi>>()

        quranSurah.value = dummyQuranSurah
        `when`(repository.fetchReadSurahEn(1)).thenReturn(quranSurah)

        viewModel.readSurahEn.observeForever(observer)

        verify(observer).onChanged(dummyQuranSurah)
    }

}