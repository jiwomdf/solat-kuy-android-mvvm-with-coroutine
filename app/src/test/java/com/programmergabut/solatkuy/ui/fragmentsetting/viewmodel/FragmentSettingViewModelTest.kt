package com.programmergabut.solatkuy.ui.fragmentsetting.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.verify
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyArgument
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.ui.fragmentsetting.FragmentSettingViewModel
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
    private lateinit var prayerRepository: PrayerRepository

    private val msApi1 = DummyArgument.msApi1

    @Before
    fun setUp() {
        viewModel = FragmentSettingViewModel(prayerRepository)
        verify(prayerRepository).getMsApi1()
    }

    @Test
    fun updateMsApi1() = coroutinesTestRule.testDispatcher.runBlockingTest{
        viewModel.updateMsApi1(msApi1)
        verify(prayerRepository).updateMsApi1(msApi1)
    }

}