package com.programmergabut.solatkuy.quran.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.filters.SmallTest
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.data.local.dao.MsConfigurationDao
import com.programmergabut.solatkuy.data.local.localentity.MsConfiguration
import com.programmergabut.solatkuy.quran.getOrAwaitValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.Is.`is`
import org.junit.*
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class MsConfigurationDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExperimentalCoroutinesApi = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: SolatKuyRoom

    private lateinit var msConfigurationDao: MsConfigurationDao

    @Before
    fun setup(){
        hiltRule.inject()
        msConfigurationDao = database.msConfigurationDao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun observeMsConfiguration() = runBlockingTest {
        val testData = MsConfiguration(1, "123" , "123", "11", "3", "2020")
        msConfigurationDao.insertMsConfiguration(testData)
        val msConfiguration = msConfigurationDao.observeMsConfiguration().getOrAwaitValue()
        assertThat(msConfiguration.api1ID, `is`(testData.api1ID))
        assertThat(msConfiguration.latitude, `is`(testData.latitude))
        assertThat(msConfiguration.longitude, `is`(testData.longitude))
    }

    @Test
    fun getMsConfiguration() = runBlockingTest {
        val testData = MsConfiguration(1, "123" , "123", "11", "3", "2020")
        msConfigurationDao.insertMsConfiguration(testData)
        val msConfiguration = msConfigurationDao.getMsConfiguration()!!
        assertThat(msConfiguration.api1ID, `is`(testData.api1ID) )
        assertThat(msConfiguration.latitude, `is`(testData.latitude) )
        assertThat(msConfiguration.longitude, `is`(testData.longitude) )
    }

    @Test
    fun deleteAllMsConfiguration() = runBlockingTest {
        val testData = MsConfiguration(1, "123" , "123", "11", "3", "2020")
        msConfigurationDao.insertMsConfiguration(testData)
        assertThat(msConfigurationDao.getMsConfiguration()!!.api1ID, `is`(testData.api1ID) )
        msConfigurationDao.deleteAll()
        assertNull(msConfigurationDao.getMsConfiguration())
    }

    @Test
    fun updateMsConfiguration() = runBlockingTest {
        val testData = MsConfiguration(1, "123" , "123", "11", "3", "2020")
        msConfigurationDao.insertMsConfiguration(testData)
        assertThat(msConfigurationDao.getMsConfiguration()!!.api1ID, `is`(testData.api1ID) )
        val newData = MsConfiguration(1, "321" , "321", "11", "3", "2020")
        msConfigurationDao.updateMsConfiguration(newData.api1ID, newData.latitude, newData.longitude, newData.method, newData.month, newData.year)
        assertThat(msConfigurationDao.getMsConfiguration()!!.latitude, `is`(newData.latitude))
        assertThat(msConfigurationDao.getMsConfiguration()!!.longitude, `is`(newData.longitude))
    }

    @Test
    fun updateMsConfigurationMonthAndYear() = runBlockingTest {
        val testData = MsConfiguration(1, "123" , "123", "11", "3", "2020")
        msConfigurationDao.insertMsConfiguration(testData)
        assertThat(msConfigurationDao.getMsConfiguration()!!.api1ID, `is`(testData.api1ID) )

        val newMonth = "4"
        val newYear = "2021"
        msConfigurationDao.updateMsConfigurationMonthAndYear(1, newMonth, newYear)
        assertThat(msConfigurationDao.getMsConfiguration()!!.month, `is`(newMonth))
        assertThat(msConfigurationDao.getMsConfiguration()!!.year, `is`(newYear))
    }

}