package com.programmergabut.solatkuy.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.filters.SmallTest
import com.programmergabut.solatkuy.data.local.dao.MsFavSurahDao
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.getOrAwaitValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class MsFavMsSurahDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExperimentalCoroutinesApi = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: SolatKuyRoom

    private lateinit var msFavSurahDao: MsFavSurahDao

    @Before
    fun setup(){
        hiltRule.inject()
        msFavSurahDao = database.msFavSurahDao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun observeListFavSurah() = runBlocking {
        val testData = MsFavSurah(1, "Al-Faatiha", "Test")
        msFavSurahDao.insertMsSurah(testData)
        val listFavSurah = msFavSurahDao.observeListFavSurah().getOrAwaitValue()
        assertThat(listFavSurah.size, `is`(1))
        assertThat(listFavSurah[0].surahName, `is`(testData.surahName))
    }

    @Test
    fun observeFavSurahBySurahID() = runBlocking {
        val testData = MsFavSurah(1, "Al-Faatiha", "Test")
        msFavSurahDao.insertMsSurah(testData)
        val favSurah = msFavSurahDao.observeFavSurahBySurahID(1).getOrAwaitValue()
        assertThat(favSurah?.surahID, `is`(testData.surahID))
        assertThat(favSurah?.surahName, `is`(testData.surahName))
        assertThat(favSurah?.surahTranslation, `is`(testData.surahTranslation))
    }

    @Test
    fun deleteAll() = runBlocking {
        val testData1 = MsFavSurah(1, "Al-Faatiha", "The Opening")
        val testData2 = MsFavSurah(2, "Al-Baqara", "The Cow")
        msFavSurahDao.insertMsSurah(testData1)
        msFavSurahDao.insertMsSurah(testData2)
        val listFavSurah = msFavSurahDao.observeListFavSurah().getOrAwaitValue()
        assertThat(listFavSurah.size, `is`(2))
        msFavSurahDao.deleteAll()
        val newListFavSurah = msFavSurahDao.observeListFavSurah().getOrAwaitValue()
        assertThat(newListFavSurah.size, `is`(0))
    }

    @Test
    fun deleteMsFavSurah() = runBlocking {
        val testData1 = MsFavSurah(1, "Al-Faatiha", "The Opening")
        msFavSurahDao.insertMsSurah(testData1)
        val listFavSurah = msFavSurahDao.observeListFavSurah().getOrAwaitValue()
        assertThat(listFavSurah.size, `is`(1))
        msFavSurahDao.deleteMsFavSurah(testData1)
        val newListFavSurah = msFavSurahDao.observeListFavSurah().getOrAwaitValue()
        assertThat(newListFavSurah.size, `is`(0))
    }

    @Test
    fun insertMsSurah() = runBlocking {
        val testData1 = MsFavSurah(1, "Al-Faatiha", "The Opening")
        msFavSurahDao.insertMsSurah(testData1)
        val listFavSurah = msFavSurahDao.observeListFavSurah().getOrAwaitValue()
        assertThat(listFavSurah.size, `is`(1))
    }


}