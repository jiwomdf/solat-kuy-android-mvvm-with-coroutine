package com.programmergabut.solatkuy.ui.fragmentcompass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.IdlingRegistry
import com.programmergabut.solatkuy.launchFragmentInHiltContainer
import com.programmergabut.solatkuy.ui.SolatKuyFragmentFactoryAndroidTest
import com.programmergabut.solatkuy.util.idlingresource.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject


@HiltAndroidTest
@ExperimentalCoroutinesApi
class CompassFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: SolatKuyFragmentFactoryAndroidTest

    @Before
    fun setUp() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @Test
    fun test_visibility_fragmentCompass(){

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