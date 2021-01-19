package com.programmergabut.solatkuy.ui.fragmentsetting

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.DummyRetValueTest
import com.programmergabut.solatkuy.data.FakePrayerRepository
import com.programmergabut.solatkuy.ui.main.fragmentsetting.FragmentSettingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SettingFragmentViewModelTest {

    private lateinit var viewModel: FragmentSettingViewModel

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var prayerRepository: FakePrayerRepository

    private val msApi1 = DummyRetValueTest.msApi1

    @Before
    fun setUp() {
        viewModel = FragmentSettingViewModel(prayerRepository)
        verify(prayerRepository).observeMsApi1()
    }

    @Test
    fun updateMsApi1() = coroutinesTestRule.testDispatcher.runBlockingTest{
        viewModel.updateMsApi1(msApi1)
        verify(prayerRepository).updateMsApi1(msApi1)
    }

}