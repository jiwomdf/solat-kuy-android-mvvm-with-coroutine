package com.programmergabut.solatkuy.ui.fragmentquran

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.programmergabut.solatkuy.R
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


@ExperimentalCoroutinesApi
@HiltAndroidTest
class QuranFragmentTest{

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
    fun test_visibility(){
        var testViewModel: QuranFragmentViewModel? = null
        launchFragmentInHiltContainer<QuranFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.et_search)).check(matches(isDisplayed()))
        onView(withId(R.id.s_juzz)).check(matches(isDisplayed()))
        onView(withId(R.id.cv_fav_ayah)).check(matches(isDisplayed()))
        onView(withId(R.id.cv_quran_content)).check(matches(isDisplayed()))
        //onView(withId(R.id.rv_stared_ayah)).check(matches(isDisplayed()))
    }

    @Test
    fun test_scroll_rv_quran(){
        var testViewModel: QuranFragmentViewModel? = null
        launchFragmentInHiltContainer<QuranFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.rv_quran_surah)).perform(RecyclerViewActions
            .actionOnItemAtPosition<RecyclerView.ViewHolder>(113, scrollTo())) //total Al Qur'an surah from idx 0
    }

    @Test
    fun test_open_first_surah_then_click_favorite(){
        var testViewModel: QuranFragmentViewModel? = null
        launchFragmentInHiltContainer<QuranFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.rv_quran_surah)).perform(RecyclerViewActions
            .actionOnItemAtPosition<RecyclerView.ViewHolder>(113, click())) //click last surah

        onView(withId(R.id.ab_readQuran)).check(matches(isDisplayed()))
        onView(withId(R.id.tb_readSurah)).check(matches(isDisplayed()))
        //openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        onView(withId(R.id.i_star_surah)).perform(click())

        Espresso.pressBack()

        Thread.sleep(5000)
    }

    @Test
    fun test_change_juzz(){
        var testViewModel: QuranFragmentViewModel? = null
        launchFragmentInHiltContainer<QuranFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }
    }

    @Test
    fun test_open_last_surah_than_swipe_left(){
        var testViewModel: QuranFragmentViewModel? = null
        launchFragmentInHiltContainer<QuranFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.rv_quran_surah)).perform(RecyclerViewActions
            .actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())) //click last surah

        onView(withId(R.id.rv_read_surah)).perform(RecyclerViewActions
            .actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeLeft())) //swipe first ayah

        Espresso.pressBack()

        onView(withId(R.id.iv_last_read_ayah)).perform(click())
    }
}
