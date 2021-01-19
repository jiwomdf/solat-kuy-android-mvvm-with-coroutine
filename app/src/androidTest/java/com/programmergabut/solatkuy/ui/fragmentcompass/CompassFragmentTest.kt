package com.programmergabut.solatkuy.ui.fragmentcompass

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.programmergabut.solatkuy.TaskExecutorWithIdlingResourceRule
import com.programmergabut.solatkuy.launchFragmentInHiltContainer
import com.programmergabut.solatkuy.ui.SolatKuyFragmentFactoryAndroidTest
import com.programmergabut.solatkuy.ui.main.fragmentcompass.CompassFragment
import com.programmergabut.solatkuy.ui.main.fragmentcompass.FragmentCompassViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class CompassFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = TaskExecutorWithIdlingResourceRule()

    @Inject
    lateinit var fragmentFactory: SolatKuyFragmentFactoryAndroidTest

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testVisibilityFragmentCompass(){

        var testViewModel: FragmentCompassViewModel? = null
        launchFragmentInHiltContainer<CompassFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        //val uiDevice: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        //val button = uiDevice.findObject(UiSelector().text("Hide"))
        //if (button.exists() && button.isEnabled) {
        //    button.click()
        //}

        //onView(withId(R.id.cv_qiblaDegrees)).check(matches(isDisplayed()))
        //onView(withId(R.id.tv_qibla_dir)).check(matches(isDisplayed()))
        //onView(withId(R.id.iv_compass)).check(matches(isDisplayed()))
        //onView(withId(R.id.tv_compass_cuation)).check(matches(isDisplayed()))
    }

}