package com.programmergabut.solatkuy.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.data.PrayerRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainActivityViewModelTest {

    private lateinit var viewModel: MainActivityViewModel

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var prayerRepositoryImpl: PrayerRepositoryImpl


    @Before
    fun setUp() {
        viewModel = MainActivityViewModel(prayerRepositoryImpl)
    }

    @Test
    fun getMsSetting(){
        Mockito.verify(prayerRepositoryImpl).getMsSetting()
    }
}