package com.programmergabut.solatkuy.ui.fragmentquran

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.TaskExecutorWithIdlingResourceRule
import com.programmergabut.solatkuy.launchFragmentInHiltContainer
import com.programmergabut.solatkuy.ui.SolatKuyFragmentFactoryAndroidTest
import com.programmergabut.solatkuy.ui.main.fragmentreadsurah.ReadSurahAdapter
import com.programmergabut.solatkuy.ui.main.fragmentquran.QuranFragment
import com.programmergabut.solatkuy.ui.main.fragmentquran.QuranFragmentDirections
import com.programmergabut.solatkuy.ui.main.fragmentquran.QuranFragmentViewModel
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.core.IsCollectionContaining.hasItems
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class QuranFragmentTest{

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
    fun testVisibility(){
        var testViewModel: QuranFragmentViewModel? = null
        launchFragmentInHiltContainer<QuranFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.et_search)).check(matches(isDisplayed()))
        onView(withId(R.id.s_juzz)).check(matches(isDisplayed()))
        onView(withId(R.id.cv_fav_ayah)).check(matches(isDisplayed()))
        onView(withId(R.id.cv_quran_content)).check(matches(isDisplayed()))
    }

    @Test
    fun testScrollRvQuran(){
        var testViewModel: QuranFragmentViewModel? = null
        launchFragmentInHiltContainer<QuranFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }
        onView(withId(R.id.rv_quran_surah)).perform(
            RecyclerViewActions
                .actionOnItemAtPosition<RecyclerView.ViewHolder>(113, scrollTo())
        )
    }

    @Test
    fun testOpenFirstSurah_thenNNavigateToReadSurahFragment(){
        val navController = mock(NavController::class.java)
        var testViewModel: QuranFragmentViewModel? = null
        launchFragmentInHiltContainer<QuranFragment>(fragmentFactory = fragmentFactory) {
            Navigation.setViewNavController(requireView(), navController)
            testViewModel = viewModel
        }
        onView(withId(R.id.rv_quran_surah)).perform(
            RecyclerViewActions
                .actionOnItemAtPosition<ReadSurahAdapter.ReadSurahViewHolder>(113, click())
        )

        val data = testViewModel?.allSurah?.value?.data?.last()!!
        verify(navController).navigate(
            QuranFragmentDirections.actionQuranFragmentToReadSurahActivity(
            data.number.toString(), data.englishName, data.englishNameTranslation, false
        ))
    }

    @Test
    fun testChangeJuzz(){
        var testViewModel: QuranFragmentViewModel? = null
        launchFragmentInHiltContainer<QuranFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.s_juzz)).perform(click())
        onData(anything()).atPosition(1).perform(click())
        Assert.assertEquals(testViewModel?.allSurah?.value?.data?.size, 2)

        onView(withId(R.id.s_juzz)).perform(click())
        onData(anything()).atPosition(15).perform(click())
        Assert.assertEquals(testViewModel?.allSurah?.value?.data?.size, 2)

        onView(withId(R.id.s_juzz)).perform(click())
        onData(anything()).atPosition(30).perform(click())
        Assert.assertEquals(testViewModel?.allSurah?.value?.data?.size, 37)
    }

    @Test
    fun testSearchSurah_recyclerViewChangeAndDataIsSameWithSearchString(){
        var testViewModel: QuranFragmentViewModel? = null
        launchFragmentInHiltContainer<QuranFragment>(fragmentFactory = fragmentFactory) {
            testViewModel = viewModel
        }

        onView(withId(R.id.et_search)).perform(replaceText("Al Faatiha"))
        onView(withId(R.id.et_search)).check(matches(withText("Al Faatiha")))
        assertEquals(testViewModel?.allSurah?.value?.data?.size, 1)
        assertEquals(testViewModel?.allSurah?.value?.data!![0].englishName, "Al-Faatiha")

        onView(withId(R.id.et_search)).perform(replaceText("Al Baqara"))
        onView(withId(R.id.et_search)).check(matches(withText("Al Baqara")))
        assertEquals(testViewModel?.allSurah?.value?.data?.size, 1)
        assertEquals(testViewModel?.allSurah?.value?.data!![0].englishName, "Al-Baqara")

        onView(withId(R.id.et_search)).perform(replaceText("An Naas"))
        onView(withId(R.id.et_search)).check(matches(withText("An Naas")))
        assertEquals(testViewModel?.allSurah?.value?.data?.size, 1)
        assertEquals(testViewModel?.allSurah?.value?.data!![0].englishName, "An-Naas")
    }
}
