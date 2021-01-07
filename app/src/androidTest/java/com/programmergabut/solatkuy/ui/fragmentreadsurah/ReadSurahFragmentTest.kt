package com.programmergabut.solatkuy.ui.fragmentreadsurah

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.programmergabut.solatkuy.DummyRetValueAndroidTest
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.getOrAwaitValue
import com.programmergabut.solatkuy.launchFragmentInHiltContainer
import com.programmergabut.solatkuy.ui.SolatKuyFragmentFactoryAndroidTest
import com.programmergabut.solatkuy.util.idlingresource.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class ReadSurahFragmentTest {

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
    fun testVisibilityAndData(){
        val initData = DummyRetValueAndroidTest.fetchAllSurah<ReadSurahFragmentTest>().data.last()
        val arg = Bundle()
        arg.putString("selectedSurahId",initData.number.toString())
        arg.putString("selectedSurahName", initData.englishName)
        arg.putString("selectedTranslation", initData.englishNameTranslation)
        arg.putBoolean("isAutoScroll", false)
        launchFragmentInHiltContainer<ReadSurahFragment>(
            fragmentArgs = arg,
            fragmentFactory = fragmentFactory
        ) {}

        onView(withId(R.id.ab_readQuran)).check(matches(isDisplayed()))
        onView(withId(R.id.tb_readSurah)).check(matches(isDisplayed()))
        onView(withId(R.id.i_star_surah)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_read_surah)).check(matches(isDisplayed()))
        onView(withId(R.id.fab_brightness)).check(matches(isDisplayed()))
    }

    @Test
    fun testScrollToTheLastAyah(){
        var testViewModel: ReadSurahViewModel? = null
        val initData = DummyRetValueAndroidTest.fetchAllSurah<ReadSurahFragmentTest>().data.last()
        val arg = Bundle()
        arg.putString("selectedSurahId",initData.number.toString())
        arg.putString("selectedSurahName", initData.englishName)
        arg.putString("selectedTranslation", initData.englishNameTranslation)
        arg.putBoolean("isAutoScroll", false)

        launchFragmentInHiltContainer<ReadSurahFragment>(
            fragmentArgs = arg,
            fragmentFactory = fragmentFactory
        ){
            testViewModel = viewModel
        }

        onView(withId(R.id.rv_read_surah)).check(matches(isDisplayed()))

        val selectedSurahAr = testViewModel!!.selectedSurahAr.getOrAwaitValue()
        val totalAyah = selectedSurahAr.data!!.data.ayahs.size - 1

        onView(withId(R.id.rv_read_surah)).perform(
            RecyclerViewActions
                .actionOnItemAtPosition<RecyclerView.ViewHolder>(totalAyah, ViewActions.scrollTo())
        )
    }

    @Test
    fun testOpenFirstSurah_thenClickFavorite_assertMsFavSurahHasChange(){
        var testViewModel: ReadSurahViewModel? = null
        val initData = DummyRetValueAndroidTest.fetchAllSurah<ReadSurahFragmentTest>().data.last()
        val arg = Bundle()
        arg.putString("selectedSurahId",initData.number.toString())
        arg.putString("selectedSurahName", initData.englishName)
        arg.putString("selectedTranslation", initData.englishNameTranslation)
        arg.putBoolean("isAutoScroll", false)
        launchFragmentInHiltContainer<ReadSurahFragment>(fragmentArgs = arg, fragmentFactory = fragmentFactory){
            testViewModel = viewModel
        }

        val prevData = testViewModel?.msFavSurah?.getOrAwaitValue()
        onView(withId(R.id.i_star_surah)).perform(click())
        val newData = testViewModel?.msFavSurah?.getOrAwaitValue()

        assertNotEquals(prevData, newData)
    }

    @Test
    fun test_open_last_surah_than_swipe_left(){
        var testViewModel: ReadSurahViewModel? = null
        val initData = DummyRetValueAndroidTest.fetchAllSurah<ReadSurahFragmentTest>().data.last()
        val arg = Bundle()
        arg.putString("selectedSurahId",initData.number.toString())
        arg.putString("selectedSurahName", initData.englishName)
        arg.putString("selectedTranslation", initData.englishNameTranslation)
        arg.putBoolean("isAutoScroll", false)
        launchFragmentInHiltContainer<ReadSurahFragment>(fragmentArgs = arg, fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.rv_read_surah)).perform(
            RecyclerViewActions
                .actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeLeft())
        ) //swipe first ayah
    }

}