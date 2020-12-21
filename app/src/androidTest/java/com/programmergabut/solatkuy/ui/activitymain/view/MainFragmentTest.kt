package com.programmergabut.solatkuy.ui.activitymain.view

import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.ui.activitymain.MainActivity
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
class MainFragmentTest{

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
    fun firstOpenFragmentMain(){

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            it.findNavController(R.id.navHostFragment).navigate(R.id.fragmentMain)
        }

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

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            it.findNavController(R.id.navHostFragment).navigate(R.id.fragmentMain)
        }

        onView(withId(R.id.tv_quran_ayah_quote_click)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_quran_ayah_quote_click)).perform(click())
        Thread.sleep(2000)

        onView(withId(R.id.tv_quran_ayah_quote)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_quran_ayah_quote)).perform(click())
        Thread.sleep(2000)
    }

    @Test
    fun clickCbPrayer(){

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            it.findNavController(R.id.navHostFragment).navigate(R.id.fragmentMain)
        }

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
    fun refreshQuote(){
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            it.findNavController(R.id.navHostFragment).navigate(R.id.fragmentMain)
        }

        onView(withId(R.id.iv_refresh)).perform(click())
    }

}