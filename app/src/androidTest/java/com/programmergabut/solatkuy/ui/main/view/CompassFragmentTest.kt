package com.programmergabut.solatkuy.ui.main.view

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.rule.ActivityTestRule
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.ui.fragmentcompass.FragmentCompass
import com.programmergabut.solatkuy.ui.main.MainActivity
import com.programmergabut.solatkuy.util.EspressoIdlingResource
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CompassFragmentTest {


    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @Test
    fun testEventFragment() {
        // The "fragmentArgs" and "factory" arguments are optional.
    }


    @Test
    fun firstOpenFragmentQompass() {

        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()))

        onView(withId(R.id.i_compass)).check(matches(isDisplayed()))

        /* Still Error

         onView(withId(R.id.bottom_navigation)).perform(NavigationViewActions.navigateTo(R.id.i_compass));


        //onView(withId(R.id.i_compass)).perform(click())

        Thread.sleep(6000)
        onView(withId(R.id.cv_qiblaDegrees)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_compass)).check(matches(isDisplayed()))
        onView(withId(R.id.textView4)).check(matches(isDisplayed()))
        //popup
        onView(withId(R.id.btn_hideAnimation)).check(matches(isDisplayed()))
        onView(withId(R.id.textView)).check(matches(isDisplayed())) */


    }

}