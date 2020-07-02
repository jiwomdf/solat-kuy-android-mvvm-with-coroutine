package com.programmergabut.solatkuy.ui.fragmentsetting.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.verify
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyArgument
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.ui.fragmentsetting.FragmentSettingViewModel
import com.programmergabut.solatkuy.DummyRetValue
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FragmentSettingViewModelTest {

    private lateinit var viewModel: FragmentSettingViewModel

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var repository: Repository

    private val msApi1 = DummyArgument.msApi1

    @Before
    fun setUp() {
        viewModel = FragmentSettingViewModel(repository)
    }

    @Test
    fun getMsApi1() {
        /* val observer = mock<Observer<MsApi1>>() */
        val dummyMsApi1 = DummyRetValue.getMsApi1()
        val msApi1 = MutableLiveData<MsApi1>()

        msApi1.value = dummyMsApi1
        //`when`(repository.getMsApi1()).thenReturn(msApi1)

        verify(repository).getMsApi1()
        assertNotNull(msApi1.value)

        /* viewModel.msApi1().observeForever(observer)
        verify(observer).onChanged(dummyMsApi1) */
    }

    @Test
    fun updateMsApi1() = coroutinesTestRule.testDispatcher.runBlockingTest{
        viewModel.updateMsApi1(msApi1)
        verify(repository).updateMsApi1(msApi1)
    }

}