package com.programmergabut.solatkuy.ui.fragmentcompass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.ui.launchFragmentInHiltContainer
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
import javax.inject.Inject
import javax.inject.Named

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class FragmentCompassTest {

    /* @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java) */

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.espressoTestIdlingResource)
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @Test
    fun test_visibility_fragmentCompass(){

        launchFragmentInHiltContainer<FragmentCompass>() {

        } // it can be done because the HiltExt.kt file


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