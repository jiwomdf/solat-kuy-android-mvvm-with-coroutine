package com.programmergabut.solatkuy.ui.fragmentinfo

import androidx.navigation.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.ui.DummyRetval
import com.programmergabut.solatkuy.ui.main.MainActivity
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
class FragmentInfoTest{
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

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
    fun test_visibility_firstOpenFragmentInfo() {

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            it.findNavController(R.id.navHostFragment).navigate(R.id.fragmentInfo)
        }

        onView(ViewMatchers.withId(R.id.tv_imsak_info_title)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.tv_imsak_date)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.tv_city)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.tv_imsak_time)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.tv_gregorian_date)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.tv_hijri_date)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.tv_gregorian_month)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.tv_hijri_month)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.tv_gregorian_day)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.tv_hijri_day)).check(matches(isDisplayed()))
    }

    @Test
    fun test_verify_fragmentInfo() {

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            it.findNavController(R.id.navHostFragment).navigate(R.id.fragmentInfo)
        }

        val topCard = DummyRetval.fragmentInfoTopCard()
        onView(ViewMatchers.withId(R.id.tv_imsak_info_title)).check(matches(withText(topCard["tv_imsak_info_title"])))
        onView(ViewMatchers.withId(R.id.tv_imsak_date)).check(matches(withText(topCard["tv_imsak_date"])))
        onView(ViewMatchers.withId(R.id.tv_city)).check(matches(withText(topCard["tv_city"])))
        onView(ViewMatchers.withId(R.id.tv_imsak_time)).check(matches(withText(topCard["tv_imsak_time"])))

        val dateCard = DummyRetval.fragmentInfoGregorianHijriCard()
        onView(ViewMatchers.withId(R.id.tv_gregorian_date)).check(matches(withText(dateCard["tv_gregorian_date"])))
        onView(ViewMatchers.withId(R.id.tv_hijri_date)).check(matches(withText(dateCard["tv_hijri_date"])))
        onView(ViewMatchers.withId(R.id.tv_gregorian_month)).check(matches(withText(dateCard["tv_gregorian_month"])))
        //onView(ViewMatchers.withId(R.id.tv_hijri_month)).check(matches(withText(dateCard[""])))
        onView(ViewMatchers.withId(R.id.tv_gregorian_day)).check(matches(withText(dateCard["tv_gregorian_day"])))
        //onView(ViewMatchers.withId(R.id.tv_hijri_day)).check(matches(withText(dateCard["tv_hijri_day"])))
    }
}