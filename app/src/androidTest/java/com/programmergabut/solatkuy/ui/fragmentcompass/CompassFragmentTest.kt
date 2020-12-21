package com.programmergabut.solatkuy.ui.fragmentcompass

import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.programmergabut.solatkuy.util.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class CompassFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @Test
    fun test_visibility_fragmentCompass(){

        /* val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            it.findNavController(R.id.navHostFragment).navigate(R.id.fragmentCompass)
        }

        onView(ViewMatchers.withId(R.id.cv_qiblaDegrees)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.tv_qibla_dir)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.iv_compass)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.tv_compass_cuation)).check(matches(isDisplayed())) */
    }

}