package com.programmergabut.solatkuy.ui.main.setting

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.data.FakePrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsConfiguration
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

    private lateinit var viewModel: SettingViewModel
    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()
    @get:Rule
    val coroutinesTestRule: CoroutinesTestRule = CoroutinesTestRule()
    @Mock
    private lateinit var prayerRepository: FakePrayerRepository

    @Before
    fun setUp() {
        viewModel = SettingViewModel(prayerRepository)
        verify(prayerRepository).observeMsConfiguration()
    }

    @Test
    fun `update MsConfiguration with correct parameter, message return success`() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val msConfiguration = MsConfiguration(1,"123","123","3","3","2020")
        val successChangeTheCoordinate = "Success change the coordinate"
        viewModel.updateMsConfiguration(msConfiguration)
        verify(prayerRepository).updateMsConfiguration(msConfiguration)
    }

    /* @Test
    fun `update MsConfiguration with incorrect format, message return error`() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val latitudeAndLongitudeCannotBeEmpty = "latitude and longitude cannot be empty"
        val latitudeAndLongitudeCannotBeStartedWithDot = "latitude and longitude cannot be started with ."
        val latitudeAndLongitudeCannotBeEndedWithDot = "latitude and longitude cannot be ended with ."

        var msConfiguration = MsConfiguration(1,"","123","3","3","2020")
        viewModel.updateMsConfiguration(msConfiguration)
        assertEquals(viewModel.updateMessage.value?.message, latitudeAndLongitudeCannotBeEmpty)

        msConfiguration = MsConfiguration(1,".123","123","3","3","2020")
        viewModel.updateMsConfiguration(msConfiguration)
        assertEquals(viewModel.updateMessage.value?.message, latitudeAndLongitudeCannotBeStartedWithDot)

        msConfiguration = MsConfiguration(1,"123","123.","3","3","2020")
        viewModel.updateMsConfiguration(msConfiguration)
        assertEquals(viewModel.updateMessage.value?.message, latitudeAndLongitudeCannotBeEndedWithDot)
    } */

}