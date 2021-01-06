package com.programmergabut.solatkuy.ui.fragmentmain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.programmergabut.solatkuy.util.idlingresource.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed

import androidx.test.filters.MediumTest
import com.programmergabut.android_jetpack_testing.getOrAwaitValue
import com.programmergabut.solatkuy.*
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Data
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.ui.EnumConfigAndroidTesting
import com.programmergabut.solatkuy.ui.SolatKuyFragmentFactoryAndroidTest
import com.programmergabut.solatkuy.ui.nestedScrollTo
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class MainFragmentTest{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fragmentFactory: SolatKuyFragmentFactoryAndroidTest

    @Before
    fun test_setUp() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @After
    fun test_tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.espressoTestIdlingResource)
    }

    @Test
    fun test_widget_visibility_and_data(){
        var testViewModel: FragmentMainViewModel? = null
        launchFragmentInHiltContainer<MainFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.tv_view_city)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_view_latitude)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_view_longitude)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_widget_prayer_countdown)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_widget_prayer_name)).check(matches(isDisplayed()))

        val data = DummyRetValueAndroidTest.getMsApi1()
        onView(withId(R.id.tv_view_city)).check(matches(withText("Kota Surakarta")))
        onView(withId(R.id.tv_view_latitude)).check(matches(withText("${data.latitude} °N")))
        onView(withId(R.id.tv_view_longitude)).check(matches(withText("${data.longitude} °W")))
    }

    @Test
    fun test_visibility_quote(){
        var testViewModel: FragmentMainViewModel? = null
        launchFragmentInHiltContainer<MainFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.tv_quran_ayah_quote_click)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_quote_setting)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_refresh)).check(matches(isDisplayed()))
    }

    @Test
    fun test_visibility_and_data_prayer(){
        var testViewModel: FragmentMainViewModel? = null
        launchFragmentInHiltContainer<MainFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.include_prayer_time))
            .perform(nestedScrollTo())

        onView(withId(R.id.cb_fajr)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_dhuhr)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_asr)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_maghrib)).check(matches(isDisplayed()))
        onView(withId(R.id.cb_isha)).check(matches(isDisplayed()))

        val prayers = testViewModel?.notifiedPrayer?.getOrAwaitValue()
        prayers?.data?.forEach { prayer ->
            when(prayer.prayerName){
                EnumConfigAndroidTesting.FAJR -> {
                    onView(withId(R.id.tv_fajr_time)).check(matches(withText(prayer.prayerTime)))
                }
                EnumConfigAndroidTesting.DHUHR -> {
                    onView(withId(R.id.tv_dhuhr_time)).check(matches(withText(prayer.prayerTime)))
                }
                EnumConfigAndroidTesting.ASR -> {
                    onView(withId(R.id.tv_asr_time)).check(matches(withText(prayer.prayerTime)))
                }
                EnumConfigAndroidTesting.MAGHRIB -> {
                    onView(withId(R.id.tv_maghrib_time)).check(matches(withText(prayer.prayerTime)))
                }
                EnumConfigAndroidTesting.ISHA -> {
                    onView(withId(R.id.tv_isha_time)).check(matches(withText(prayer.prayerTime)))
                }
                else -> {}
            }
        }
    }

    @Test
    fun test_visibility_and_data_info(){
        var testViewModel: FragmentMainViewModel? = null
        launchFragmentInHiltContainer<MainFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.include_info))
            .perform(nestedScrollTo())

        onView(withId(R.id.tv_imsak_info_title)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_imsak_date)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_city)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_imsak_time)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_gregorian_date)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_hijri_date)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_gregorian_month)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_hijri_month)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_gregorian_day)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_hijri_day)).check(matches(isDisplayed()))

        onView(withId(R.id.rvDuaCollection)).check(matches(isDisplayed()))

        val sdf = SimpleDateFormat("dd", Locale.getDefault())
        val currentDate = sdf.format(Date())
        val response = testViewModel?.prayer?.getOrAwaitValue()
        val data = createTodayData(response?.data, currentDate)
        val date = data?.date
        val hijriDate = date?.hijri
        val gregorianDate = date?.gregorian

        onView(withId(R.id.tv_imsak_info_title)).check(matches(withText("Imsak Info")))
        onView(withId(R.id.tv_imsak_time)).check(matches(withText(data?.timings?.imsak)))

        onView(withId(R.id.tv_gregorian_date)).check(matches(withText(gregorianDate?.date)))
        onView(withId(R.id.tv_hijri_date)).check(matches(withText(hijriDate?.date)))
        onView(withId(R.id.tv_gregorian_month)).check(matches(withText(gregorianDate?.month?.en)))
        onView(withId(R.id.tv_hijri_month)).check(matches(withText(hijriDate?.month?.en + " / " + hijriDate?.month?.ar)))
        onView(withId(R.id.tv_gregorian_day)).check(matches(withText(gregorianDate?.weekday?.en)))
        onView(withId(R.id.tv_hijri_day)).check(matches(withText(hijriDate?.weekday?.en + " / " + hijriDate?.weekday?.ar)))
    }

    private fun createTodayData(it: PrayerResponse?, currentDate: String): Data? {
        return it?.data?.find { obj -> obj.date.gregorian?.day == currentDate }
    }

    @Test
    fun test_click_quran_quote() {
        var testViewModel: FragmentMainViewModel? = null
        launchFragmentInHiltContainer<MainFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.tv_quran_ayah_quote_click)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_quran_ayah_quote_click)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.tv_quran_ayah_quote)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_quran_ayah_quote)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_quran_ayah_quote)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.tv_quran_ayah_quote_click)).check(matches(isDisplayed()))
    }

    @Test
    fun test_click_cb_prayer(){
        var testViewModel: FragmentMainViewModel? = null
        launchFragmentInHiltContainer<MainFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.include_prayer_time))
            .perform(nestedScrollTo())

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

        val prayers = testViewModel?.notifiedPrayer?.getOrAwaitValue()
        prayers?.data?.forEach { prayer ->
            if(prayer.prayerName == EnumConfigAndroidTesting.SUNRISE)
                return@forEach

            assertEquals(false, prayer.isNotified)
        }
    }

    @Test
    fun test_refresh_quote(){
        var testViewModel: FragmentMainViewModel? = null
        launchFragmentInHiltContainer<MainFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.iv_refresh)).perform(click())
    }

}