package com.programmergabut.solatkuy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.util.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

@Database(version = 4, entities =
[MsNotifiedPrayer::class, MsConfiguration::class, MsSetting::class, MsFavSurah::class,
    MsSurah::class, MsAyah::class, MsCalculationMethods::class, FastRataItemEntity::class])
abstract class SolatKuyRoom : RoomDatabase() {

    abstract fun notifiedPrayerDao(): MsNotifiedPrayerDao
    abstract fun msConfigurationDao(): MsConfigurationDao
    abstract fun msSettingDao(): MsSettingDao
    abstract fun msSurahDao(): MsSurahDao
    abstract fun msAyahDao(): MsAyahDao
    abstract fun msFavSurahDao(): MsFavSurahDao
    abstract fun msCalculationMethodsDao(): MsCalculationMethodsDao
    abstract fun fastRataDao(): FastRataDao

    companion object{
        fun populateDatabase(instance: SolatKuyRoom){
            GlobalScope.launch(Dispatchers.IO) {
                populateMsSetting(instance.msSettingDao())
                populateNotifiedPrayer(instance.notifiedPrayerDao())
                populateMsConfiguration(instance.msConfigurationDao())
            }
        }

        private suspend fun populateMsSetting(msSettingDao: MsSettingDao){
            msSettingDao.deleteAll()
            msSettingDao.insertMsSetting(MsSetting(1, false))
        }

        private suspend fun populateMsConfiguration(msConfigurationDao: MsConfigurationDao) {
            msConfigurationDao.deleteAll()
            msConfigurationDao.insertMsConfiguration(
                MsConfiguration(1, Constant.START_LAT, Constant.START_LNG,
                    Constant.STARTED_METHOD, Constant.START_MONTH, Constant.START_YEAR))
        }

        private suspend fun populateNotifiedPrayer(msNotifiedPrayerDao: MsNotifiedPrayerDao){
            msNotifiedPrayerDao.deleteAll()
            msNotifiedPrayerDao.insertNotifiedPrayer(
                MsNotifiedPrayer(
                    Constant.FAJR,
                    true,
                    Constant.FAJR_TIME
                )
            )
            msNotifiedPrayerDao.insertNotifiedPrayer(
                MsNotifiedPrayer(
                    Constant.DHUHR,
                    true,
                    Constant.DHUHR_TIME
                )
            )
            msNotifiedPrayerDao.insertNotifiedPrayer(
                MsNotifiedPrayer(
                    Constant.ASR,
                    true,
                    Constant.ASR_TIME
                )
            )
            msNotifiedPrayerDao.insertNotifiedPrayer(
                MsNotifiedPrayer(
                    Constant.MAGHRIB,
                    true,
                    Constant.MAGHRIB_TIME
                )
            )
            msNotifiedPrayerDao.insertNotifiedPrayer(
                MsNotifiedPrayer(
                    Constant.ISHA,
                    true,
                    Constant.ISHA_TIME
                )
            )
            msNotifiedPrayerDao.insertNotifiedPrayer(
                MsNotifiedPrayer(
                    Constant.SUNRISE,
                    true,
                    Constant.SUNRISE_TIME
                )
            )
        }

    }

}