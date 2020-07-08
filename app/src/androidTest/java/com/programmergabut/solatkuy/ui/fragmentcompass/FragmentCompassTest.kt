package com.programmergabut.solatkuy.ui.fragmentcompass

import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.ui.main.MainActivity
import com.programmergabut.solatkuy.util.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class FragmentCompassTest {

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java)

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

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            it.findNavController(R.id.navHostFragment).navigate(R.id.fragmentCompass)
        }

        /* onView(withId(R.id.btn_hideAnimation))
            .inRoot(isDialog()) // <---
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.cv_qiblaDegrees)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_qibla_dir)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_compass)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_compass_cuation)).check(matches(isDisplayed())) */
    }

    @Test
    fun test_hide_popup(){

    }
}