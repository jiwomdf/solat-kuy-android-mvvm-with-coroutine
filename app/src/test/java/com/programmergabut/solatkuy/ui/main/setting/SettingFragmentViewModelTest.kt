package com.programmergabut.solatkuy.ui.main.setting

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.programmergabut.solatkuy.CoroutinesTestRule
import com.programmergabut.solatkuy.data.FakePrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.util.SharedPrefUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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

    @Mock
    private lateinit var sharedPrefUtil: SharedPrefUtil

    @Before
    fun setUp() {
        viewModel = SettingViewModel(prayerRepository, sharedPrefUtil)
        verify(prayerRepository).observeMsApi1()
    }

    @Test
    fun `update MsApi1 with correct parameter, message return success`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            val msApi1 = MsApi1(1, "123", "123", "3", "3", "2020")
            val successChangeTheCoordinate = "Success change the coordinate"
            viewModel.updateMsApi1(msApi1)
            verify(prayerRepository).updateMsApi1(msApi1)
        }

    /* @Test
    fun `update MsApi1 with incorrect format, message return error`() = coroutinesTestRule.testDispatcher.runBlockingTest{
        val latitudeAndLongitudeCannotBeEmpty = "latitude and longitude cannot be empty"
        val latitudeAndLongitudeCannotBeStartedWithDot = "latitude and longitude cannot be started with ."
        val latitudeAndLongitudeCannotBeEndedWithDot = "latitude and longitude cannot be ended with ."

        var msApi1 = MsApi1(1,"","123","3","3","2020")
        viewModel.updateMsApi1(msApi1)
        assertEquals(viewModel.updateMessage.value?.message, latitudeAndLongitudeCannotBeEmpty)

        msApi1 = MsApi1(1,".123","123","3","3","2020")
        viewModel.updateMsApi1(msApi1)
        assertEquals(viewModel.updateMessage.value?.message, latitudeAndLongitudeCannotBeStartedWithDot)

        msApi1 = MsApi1(1,"123","123.","3","3","2020")
        viewModel.updateMsApi1(msApi1)
        assertEquals(viewModel.updateMessage.value?.message, latitudeAndLongitudeCannotBeEndedWithDot)
    } */

}