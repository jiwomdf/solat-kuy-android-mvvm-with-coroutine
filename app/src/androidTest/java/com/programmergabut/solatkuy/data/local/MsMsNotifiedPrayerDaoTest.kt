package com.programmergabut.solatkuy.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.filters.SmallTest
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
class MsMsNotifiedPrayerDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExperimentalCoroutinesApi = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: SolatKuyRoom

    lateinit var msNotifiedPrayerDao: MsNotifiedPrayerDao

    @Before
    fun setup(){
        hiltRule.inject()
        msNotifiedPrayerDao = database.notifiedPrayerDao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun observeListNotifiedPrayer() = runBlockingTest {
        val testData = MsNotifiedPrayer(1, "Fajr",  false, "04:00")
        msNotifiedPrayerDao.insertNotifiedPrayer(testData)
        val listPrayer = msNotifiedPrayerDao.observeListNotifiedPrayer().getOrAwaitValue().first()
        assertThat(listPrayer.prayerID, `is`(1))
        assertThat(listPrayer.prayerName, `is`("Fajr"))
        assertThat(listPrayer.prayerTime, `is`("04:00"))
        assertThat(listPrayer.isNotified, `is`(false))
    }

    /* @Test
    fun getListNotifiedPrayer() = runBlockingTest {
        val testData1 = NotifiedPrayer(1, "Fajr",  false, "04:00")
        val testData2 = NotifiedPrayer(2, "Dhuhr",  false, "12:00")
        notifiedPrayerDao.insertNotifiedPrayer(testData1)
        notifiedPrayerDao.insertNotifiedPrayer(testData2)
        val listPrayer = notifiedPrayerDao.getListNotifiedPrayer()
        assertThat(listPrayer?.size, `is`(2))
    } */

    @Test
    fun deleteAll() = runBlockingTest {
        val testData1 = MsNotifiedPrayer(1, "Fajr",  false, "04:00")
        val testData2 = MsNotifiedPrayer(2, "Dhuhr",  false, "12:00")
        msNotifiedPrayerDao.insertNotifiedPrayer(testData1)
        msNotifiedPrayerDao.insertNotifiedPrayer(testData2)
        /* assertThat(notifiedPrayerDao.getListNotifiedPrayer()?.size, `is`(2))
        notifiedPrayerDao.deleteAll()
        assertThat(notifiedPrayerDao.getListNotifiedPrayer()?.size, `is`(0)) */
    }

    /* @Test
    fun insertNotifiedPrayer() = runBlockingTest {
        val testData1 = NotifiedPrayer(1, "Fajr",  false, "04:00")
        notifiedPrayerDao.insertNotifiedPrayer(testData1)
        //val listPrayer = notifiedPrayerDao.getListNotifiedPrayer()?.get(0)!!
        assertThat(listPrayer.prayerID, `is`(1))
        assertThat(listPrayer.prayerName, `is`("Fajr"))
        assertThat(listPrayer.prayerTime, `is`("04:00"))
        assertThat(listPrayer.isNotified, `is`(false))
    } */

    /* @Test
    fun updateNotifiedPrayer() = runBlockingTest {
        val testData1 = NotifiedPrayer(1, "Fajr",  false, "04:00")
        notifiedPrayerDao.insertNotifiedPrayer(testData1)
        val prayer = notifiedPrayerDao.getListNotifiedPrayer()?.get(0)!!
        assertThat(prayer.isNotified, `is`(false))

        notifiedPrayerDao.updateNotifiedPrayer("Fajr",  true, "04:00")
        val newPrayer = notifiedPrayerDao.getListNotifiedPrayer()?.get(0)!!
        assertThat(newPrayer.isNotified, `is`(true))
    } */

    /* @Test
    fun updatePrayerTime() = runBlockingTest {
        val testData1 = NotifiedPrayer(1, "Fajr",  false, "04:00")
        notifiedPrayerDao.insertNotifiedPrayer(testData1)
        val prayer = notifiedPrayerDao.getListNotifiedPrayer()?.get(0)!!
        assertThat(prayer.isNotified, `is`(false))

        notifiedPrayerDao.updatePrayerTime("Fajr",   "04:30")
        val newPrayer = notifiedPrayerDao.getListNotifiedPrayer()?.get(0)!!
        assertThat(newPrayer.prayerName, `is`("Fajr"))
        assertThat(newPrayer.prayerTime, `is`("04:30"))
    } */

    /* @Test
    fun updatePrayerIsNotified() = runBlockingTest {
        val testData1 = NotifiedPrayer(1, "Fajr",  false, "04:00")
        notifiedPrayerDao.insertNotifiedPrayer(testData1)
        val prayer = notifiedPrayerDao.getListNotifiedPrayer()?.get(0)!!
        assertThat(prayer.isNotified, `is`(false))

        notifiedPrayerDao.updatePrayerIsNotified("Fajr", true)
        assertThat(notifiedPrayerDao.getListNotifiedPrayer()?.get(0)!!.isNotified, `is`(true))
    } */


}