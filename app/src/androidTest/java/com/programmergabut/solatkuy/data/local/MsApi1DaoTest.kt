package com.programmergabut.solatkuy.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.filters.SmallTest
import com.programmergabut.solatkuy.data.local.dao.MsApi1Dao
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.getOrAwaitValue
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
class MsApi1DaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExperimentalCoroutinesApi = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: SolatKuyRoom

    private lateinit var msAPi1Dao: MsApi1Dao

    @Before
    fun setup(){
        hiltRule.inject()
        msAPi1Dao = database.msApi1Dao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun observeMsApi1() = runBlockingTest {
        val testData = MsApi1(1, "123" , "123", "11", "3", "2020")
        msAPi1Dao.insertMsApi1(testData)
        val msApi1 = msAPi1Dao.observeMsApi1().getOrAwaitValue()
        assertThat(msApi1.api1ID, `is`(testData.api1ID))
        assertThat(msApi1.latitude, `is`(testData.latitude))
        assertThat(msApi1.longitude, `is`(testData.longitude))
    }

    @Test
    fun getMsApi1() = runBlockingTest {
        val testData = MsApi1(1, "123" , "123", "11", "3", "2020")
        msAPi1Dao.insertMsApi1(testData)
        val msApi1 = msAPi1Dao.getMsApi1()!!
        assertThat(msApi1.api1ID, `is`(testData.api1ID) )
        assertThat(msApi1.latitude, `is`(testData.latitude) )
        assertThat(msApi1.longitude, `is`(testData.longitude) )
    }

    @Test
    fun deleteAllMsApi1() = runBlockingTest {
        val testData = MsApi1(1, "123" , "123", "11", "3", "2020")
        msAPi1Dao.insertMsApi1(testData)
        assertThat(msAPi1Dao.getMsApi1()!!.api1ID, `is`(testData.api1ID) )
        msAPi1Dao.deleteAll()
        assertNull(msAPi1Dao.getMsApi1())
    }

    @Test
    fun updateMsApi1() = runBlockingTest {
        val testData = MsApi1(1, "123" , "123", "11", "3", "2020")
        msAPi1Dao.insertMsApi1(testData)
        assertThat(msAPi1Dao.getMsApi1()!!.api1ID, `is`(testData.api1ID) )
        val newData = MsApi1(1, "321" , "321", "11", "3", "2020")
        msAPi1Dao.updateMsApi1(newData.api1ID, newData.latitude, newData.longitude, newData.method, newData.month, newData.year)
        assertThat(msAPi1Dao.getMsApi1()!!.latitude, `is`(newData.latitude))
        assertThat(msAPi1Dao.getMsApi1()!!.longitude, `is`(newData.longitude))
    }

    @Test
    fun updateMsApi1MonthAndYear() = runBlockingTest {
        val testData = MsApi1(1, "123" , "123", "11", "3", "2020")
        msAPi1Dao.insertMsApi1(testData)
        assertThat(msAPi1Dao.getMsApi1()!!.api1ID, `is`(testData.api1ID) )

        val newMonth = "4"
        val newYear = "2021"
        msAPi1Dao.updateMsApi1MonthAndYear(1, newMonth, newYear)
        assertThat(msAPi1Dao.getMsApi1()!!.month, `is`(newMonth))
        assertThat(msAPi1Dao.getMsApi1()!!.year, `is`(newYear))
    }

}