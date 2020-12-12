package com.programmergabut.solatkuy.ui.fragmentinfo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.ui.DummyRetval
import com.programmergabut.solatkuy.ui.TestSolatKuyFragmentFactory
import com.programmergabut.solatkuy.launchFragmentInHiltContainer
import com.programmergabut.solatkuy.ui.main.MainActivity
import com.programmergabut.solatkuy.util.EspressoIdlingResource
import com.programmergabut.solatkuy.viewmodel.FakePrayerRepositoryAndroidTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@MediumTest
class InfoFragmentTest{

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var testSolatKuyFragmentFactory: TestSolatKuyFragmentFactory

    @Before
    fun setUp() {
        hiltRule.inject()
        //IdlingRegistry.getInstance().register(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @After
    fun tearDown() {
       //IdlingRegistry.getInstance().unregister(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @Test
    fun test_visibility_firstOpenFragmentInfo() {

        val testViewModel = FragmentInfoViewModel(FakePrayerRepositoryAndroidTest())
        launchFragmentInHiltContainer<InfoFragment>(fragmentFactory = testSolatKuyFragmentFactory) {
            viewModel = testViewModel
        }

        onView(withId(R.id.tv_imsak_info_title)).check(matches(isDisplayed()))
        /*onView(withId(R.id.tv_imsak_date)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_city)).check(matches(isDisplayed()))*/
        onView(withId(R.id.tv_imsak_time)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_gregorian_date)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_hijri_date)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_gregorian_month)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_hijri_month)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_gregorian_day)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_hijri_day)).check(matches(isDisplayed()))
        onView(withId(R.id.rvDuaCollection)).check(matches(isDisplayed()))

        Thread.sleep(5000)
    }

    @Test
    fun test_verify_fragmentInfo() {

        val testViewModel = FragmentInfoViewModel(FakePrayerRepositoryAndroidTest())
        launchFragmentInHiltContainer<InfoFragment>(fragmentFactory = testSolatKuyFragmentFactory) {
            viewModel = testViewModel
        }

        val topCard = DummyRetval.fragmentInfoTopCard()
        onView(withId(R.id.tv_imsak_info_title)).check(matches(withText(topCard["tv_imsak_info_title"])))
        onView(withId(R.id.tv_imsak_date)).check(matches(withText(topCard["tv_imsak_date"])))
        onView(withId(R.id.tv_city)).check(matches(withText(topCard["tv_city"])))
        onView(withId(R.id.tv_imsak_time)).check(matches(withText(topCard["tv_imsak_time"])))

        val dateCard = DummyRetval.fragmentInfoGregorianHijriCard()
        onView(withId(R.id.tv_gregorian_date)).check(matches(withText(dateCard["tv_gregorian_date"])))
        onView(withId(R.id.tv_hijri_date)).check(matches(withText(dateCard["tv_hijri_date"])))
        onView(withId(R.id.tv_gregorian_month)).check(matches(withText(dateCard["tv_gregorian_month"])))
        //onView(withId(R.id.tv_hijri_month)).check(matches(withText(dateCard[""])))
        onView(withId(R.id.tv_gregorian_day)).check(matches(withText(dateCard["tv_gregorian_day"])))
        //onView(withId(R.id.tv_hijri_day)).check(matches(withText(dateCard["tv_hijri_day"])))
    }

    @Test
    fun test_scroll_and_click_first_rvDuaCollection(){

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            it.findNavController(R.id.navHostFragment).navigate(R.id.fragmentInfo)
        }

        onView(withId(R.id.rvDuaCollection))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())) //click first surah

    }

    @Test
    fun test_refreshLayout(){
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity {
            it.findNavController(R.id.navHostFragment).navigate(R.id.fragmentInfo)
        }

        onView(withId(R.id.sl_info)).perform(ViewActions.swipeDown())
    }
}