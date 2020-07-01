package com.programmergabut.solatkuy.ui.fragmentsetting.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.verify
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.ui.fragmentsetting.FragmentSettingViewModel
import com.programmergabut.solatkuy.util.generator.DummyData
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FragmentSettingViewModelTest {

    private lateinit var viewModel: FragmentSettingViewModel

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
        viewModel =
            FragmentSettingViewModel(
                repository
            )
        viewModel.updateMsApi1(msApi1)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun getMsApi1() {
        /* val observer = mock<Observer<MsApi1>>() */
        val dummyMsApi1 = DummyData.getMsApi1()
        val msApi1 = MutableLiveData<MsApi1>()

        msApi1.value = dummyMsApi1
        //`when`(repository.getMsApi1()).thenReturn(msApi1)

        verify(repository).getMsApi1()
        assertNotNull(msApi1.value)

        /* viewModel.msApi1().observeForever(observer)
        verify(observer).onChanged(dummyMsApi1) */
    }

    @Test
    fun updateMsApi1() = runBlocking{
        verify(repository).updateMsApi1(msApi1)
    }

}