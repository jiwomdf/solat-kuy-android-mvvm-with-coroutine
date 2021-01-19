package com.programmergabut.solatkuy.ui.fragmentsetting

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.TaskExecutorWithIdlingResourceRule
import com.programmergabut.solatkuy.launchFragmentInHiltContainer
import com.programmergabut.solatkuy.ui.SolatKuyFragmentFactoryAndroidTest
import com.programmergabut.solatkuy.ui.main.fragmentsetting.FragmentSettingViewModel
import com.programmergabut.solatkuy.ui.main.fragmentsetting.SettingFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SettingFragmentTest{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    val instantTaskExecutorRule = TaskExecutorWithIdlingResourceRule()
    @Inject
    lateinit var fragmentFactory: SolatKuyFragmentFactoryAndroidTest

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun testVisibilityAndData(){
        var testViewModel: FragmentSettingViewModel? = null
        launchFragmentInHiltContainer<SettingFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.tv_cur_loc)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_view_latitude)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_text_latitude)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_view_longitude)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_text_longitude)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_view_city)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_authorCredit)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_location_logo)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_by_latitude_longitude)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_by_gps)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_changeLocation)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_author_logo)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_authorCredit)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_seeAuthor)).check(matches(isDisplayed()))

        val data = testViewModel?.msApi1?.value!!

        onView(withId(R.id.tv_cur_loc)).check(matches(withText("Current Location")))
        onView(withId(R.id.tv_view_latitude)).check(matches((withText(data.latitude + " °S"))))
        onView(withId(R.id.tv_view_longitude)).check(matches((withText(data.longitude + " °E"))))
        onView(withId(R.id.tv_view_city)).check(matches((withText("Kota Surakarta"))))
        onView(withId(R.id.tv_authorCredit)).check(matches((withText("Author & Credit"))))
        onView(withId(R.id.tv_changeLocation)).check(matches((withText("Change Location"))))
    }

}
