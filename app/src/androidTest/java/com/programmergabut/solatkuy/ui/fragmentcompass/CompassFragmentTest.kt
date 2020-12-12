package com.programmergabut.solatkuy.ui.fragmentcompass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.ui.TestSolatKuyFragmentFactory
import com.programmergabut.solatkuy.launchFragmentInHiltContainer
import com.programmergabut.solatkuy.viewmodel.FakePrayerRepositoryAndroidTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class CompassFragmentTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var testSolatKuyFragmentFactory: TestSolatKuyFragmentFactory

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun test_visibility_fragmentCompass(){

        val testViewModel = FragmentCompassViewModel(FakePrayerRepositoryAndroidTest())
        launchFragmentInHiltContainer<CompassFragment>(fragmentFactory = testSolatKuyFragmentFactory) {
            viewModel = testViewModel
        }

        /* onView(withId(R.id.btn_hideAnimation))
            .inRoot(isDialog()) // <---
            .check(matches(isDisplayed()))
            .perform(click()) */

        onView(withId(R.id.cv_qiblaDegrees)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_qibla_dir)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_compass)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_compass_cuation)).check(matches(isDisplayed()))
    }

    @Test
    fun test_hide_popup() {}
}