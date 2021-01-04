package com.programmergabut.solatkuy.ui.fragmentquran

import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
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
class QuranFragmentTest{

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
    fun test_visibility(){
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            it.findNavController(R.id.navHostFragment).navigate(R.id.quranFragment)
        }

        onView(withId(R.id.et_search)).check(matches(isDisplayed()))
        onView(withId(R.id.s_juzz)).check(matches(isDisplayed()))
        onView(withId(R.id.cv_fav_ayah)).check(matches(isDisplayed()))
        //onView(withId(R.id.rv_stared_ayah)).check(matches(isDisplayed()))
    }

    @Test
    fun test_scroll_rv_quran(){
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            it.findNavController(R.id.navHostFragment).navigate(R.id.quranFragment)
        }

        onView(withId(R.id.rv_quran_surah)).perform(RecyclerViewActions
            .scrollToPosition<RecyclerView.ViewHolder>(144)) //total Al Qur'an surah
    }

    @Test
    fun test_open_first_surah_then_click_favorite(){
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            it.findNavController(R.id.navHostFragment).navigate(R.id.quranFragment)
        }

        onView(withId(R.id.rv_quran_surah)).perform(RecyclerViewActions
            .actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())) //click last surah

        onView(withId(R.id.ab_readQuran)).check(matches(isDisplayed()))
        onView(withId(R.id.tb_readSurah)).check(matches(isDisplayed()))
        //openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withId(R.id.i_star_surah)).perform(click())
    }

    @Test
    fun test_change_juzz(){
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            it.findNavController(R.id.navHostFragment).navigate(R.id.quranFragment)
        }
    }
}
