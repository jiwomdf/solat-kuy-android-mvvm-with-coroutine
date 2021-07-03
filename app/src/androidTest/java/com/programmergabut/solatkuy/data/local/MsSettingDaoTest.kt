package com.programmergabut.solatkuy.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.filters.SmallTest
import com.programmergabut.solatkuy.data.local.dao.MsSettingDao
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.getOrAwaitValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertNull
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
class MsSettingDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExperimentalCoroutinesApi = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: SolatKuyRoom

    lateinit var msSettingDao: MsSettingDao

    @Before
    fun setup(){
        hiltRule.inject()
        msSettingDao = database.msSettingDao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun observeMsSetting() = runBlockingTest {
        val testData = MsSetting(1, isHasOpenApp = false)
        msSettingDao.insertMsSetting(testData)
        val listMsSetting = msSettingDao.observeMsSetting().getOrAwaitValue()
        assertThat(listMsSetting.no, `is`(1))
        assertThat(listMsSetting.isHasOpenApp, `is`(false))
    }

    @Test
    fun deleteAll() = runBlockingTest {
        val testData = MsSetting(1, isHasOpenApp = false)
        msSettingDao.insertMsSetting(testData)
        val listMsSetting = msSettingDao.observeMsSetting().getOrAwaitValue()
        assertThat(listMsSetting.no, `is`(1))
        msSettingDao.deleteAll()
        val newMsSetting = msSettingDao.observeMsSetting().getOrAwaitValue()
        assertNull(newMsSetting)
    }

    @Test
    fun insertMsSetting() = runBlockingTest {
        val testData = MsSetting(1, isHasOpenApp = false)
        msSettingDao.insertMsSetting(testData)
        val listMsSetting = msSettingDao.observeMsSetting().getOrAwaitValue()
        assertThat(listMsSetting.no, `is`(1))
    }

    @Test
    fun updateIsHasOpenApp() = runBlockingTest {
        val testData = MsSetting(1, isHasOpenApp = false)
        msSettingDao.insertMsSetting(testData)
        val msSetting = msSettingDao.observeMsSetting().getOrAwaitValue()
        assertThat(msSetting.no, `is`(1))

        msSettingDao.updateIsHasOpenApp(true)
        val newMsSetting = msSettingDao.observeMsSetting().getOrAwaitValue()
        assertThat(newMsSetting.no, `is`(1))
        assertThat(newMsSetting.isHasOpenApp, `is`(true))
    }

}