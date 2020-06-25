package com.programmergabut.solatkuy.ui.main.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.ui.main.MainActivity
import com.programmergabut.solatkuy.util.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class MainActivityTest{

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
    fun firstOpenFragmentMain(){

        onView(withId(R.id.tv_view_city)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_view_latitude)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_view_longitude)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_widget_prayer_countdown)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_widget_prayer_name)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_quran_ayah_quote_click)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_fajr)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_dhuhr)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_asr)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_maghrib)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_isha)).check(matches(isDisplayed()))
    }

    @Test
    fun clickQuranQuote() {
        onView(withId(R.id.tv_quran_ayah_quote_click)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_quran_ayah_quote_click)).perform(click())
        Thread.sleep(2000)

        onView(withId(R.id.tv_quran_ayah_quote)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_quran_ayah_quote)).perform(click())
        Thread.sleep(2000)
    }

    @Test
    fun clickCbPrayer(){
        onView(withId(R.id.cb_fajr)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.cb_dhuhr)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.cb_asr)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.cb_maghrib)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.cb_isha)).check(matches(isCompletelyDisplayed()))

        onView(withId(R.id.cb_fajr)).perform(click())
        onView(withId(R.id.cb_dhuhr)).perform(click())
        onView(withId(R.id.cb_asr)).perform(click())
        onView(withId(R.id.cb_maghrib)).perform(click())
        onView(withId(R.id.cb_isha)).perform(click())
    }

    @Test
    fun refreshLayout(){
        onView(withId(R.id.sl_main)).perform(swipeDown())
    }

}

