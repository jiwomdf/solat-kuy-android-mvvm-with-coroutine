package com.programmergabut.solatkuy.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.filters.SmallTest
import com.programmergabut.solatkuy.data.local.dao.MsFavAyahDao
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.getOrAwaitValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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
class MsFavAyahDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExperimentalCoroutinesApi = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: SolatKuyRoom

    private lateinit var msFavAyahDao: MsFavAyahDao

    @Before
    fun setup(){
        hiltRule.inject()
        msFavAyahDao = database.msFavAyahDao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun observeListFavAyah() = runBlockingTest{
        val testData = MsFavAyah(1, 1 , "Al-Faatiha", "test", "The Opening")
        msFavAyahDao.insertMsAyah(testData)
        val listFavAyah = msFavAyahDao.observeListFavAyah().getOrAwaitValue()
        assertThat(listFavAyah.size, `is`(1))
        assertThat(listFavAyah[0].surahName, `is`(testData.surahName))
    }

    @Test
    fun getListFavAyahBySurahID() = runBlockingTest{
        val testData = MsFavAyah(1, 1 , "Al-Faatiha", "test", "The Opening")
        msFavAyahDao.insertMsAyah(testData)
        val favAyah = msFavAyahDao.getListFavAyahBySurahID(1)!!.first()
        assertThat(favAyah.surahName, `is`(testData.surahName))
    }

    @Test
    fun deleteAll() = runBlockingTest{
        val testData = MsFavAyah(1, 1, "Al-Faatiha", "test", "The Opening")
        val testData2 = MsFavAyah(2, 2, "Al-Baqara", "test", "The Cow")
        msFavAyahDao.insertMsAyah(testData)
        msFavAyahDao.insertMsAyah(testData2)
        msFavAyahDao.deleteAll()
        val listFavAyah = msFavAyahDao.observeListFavAyah().getOrAwaitValue()
        assertThat(listFavAyah.size, `is`(0))
    }

    @Test
    fun deleteMsFavAyah() = runBlockingTest{
        val testData = MsFavAyah(1, 1 , "Al-Faatiha", "test", "The Opening")
        msFavAyahDao.insertMsAyah(testData)
        msFavAyahDao.deleteMsFavAyah(testData)
        val listFavAyah = msFavAyahDao.observeListFavAyah().getOrAwaitValue()
        assertThat(listFavAyah.size, `is`(0))
    }

    @Test
    fun deleteMsFavAyahByID() = runBlockingTest{
        val testData = MsFavAyah(1, 1 , "Al-Faatiha", "test", "The Opening")
        msFavAyahDao.insertMsAyah(testData)
        msFavAyahDao.deleteMsFavAyahByID(1, 1)
        val listFavAyah = msFavAyahDao.observeListFavAyah().getOrAwaitValue()
        assertThat(listFavAyah.size, `is`(0))
    }

    @Test
    fun insertMsAyah() = runBlockingTest{
        val testData = MsFavAyah(1, 1 , "Al-Faatiha", "test", "The Opening")
        msFavAyahDao.insertMsAyah(testData)
        val listFavAyah = msFavAyahDao.observeListFavAyah().getOrAwaitValue()
        assertThat(listFavAyah.size, `is`(1))
        assertThat(listFavAyah[0].surahID, `is`(testData.surahID))
        assertThat(listFavAyah[0].ayahID, `is`(testData.ayahID))
        assertThat(listFavAyah[0].surahName, `is`(testData.surahName))
    }

}