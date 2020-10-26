package com.programmergabut.solatkuy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

@Database(version = 12, entities = [NotifiedPrayer::class, MsApi1::class, MsSetting::class, MsFavAyah::class, MsFavSurah::class])
abstract class SolatKuyRoom: RoomDatabase() {

    abstract fun notifiedPrayerDao(): NotifiedPrayerDao
    abstract fun msApi1Dao(): MsApi1Dao
    abstract fun msSettingDao(): MsSettingDao
    abstract fun msFavAyahDao(): MsFavAyahDao
    abstract fun msFavSurahDao(): MsFavSurahDao

    companion object{
        fun populateDatabase(instance: SolatKuyRoom){
            GlobalScope.launch(Dispatchers.IO) {
                populateMsSetting(instance.msSettingDao())
                populateNotifiedPrayer(instance.notifiedPrayerDao())
                populateMsApi1(instance.msApi1Dao())
            }
        }

        private suspend fun populateMsSetting(msSettingDao: MsSettingDao){
            msSettingDao.deleteAll()

            msSettingDao.insertMsSetting(
                MsSetting(
                    1,
                    false,
                    isUsingDBQuotes = false
                )
            )
        }

        private suspend fun populateMsApi1(msApi1Dao: MsApi1Dao) {
            msApi1Dao.deleteAll()

            msApi1Dao.insertMsApi1(
                MsApi1(
                    1,
                    "0",
                    "0",
                    "3",
                    "3",
                    "2020"
                )
            )
        }

        private suspend fun populateNotifiedPrayer(notifiedPrayerDao: NotifiedPrayerDao){
            notifiedPrayerDao.deleteAll()

            notifiedPrayerDao.insertNotifiedPrayer(
                NotifiedPrayer(
                    EnumConfig.FAJR,
                    true,
                    "00:00"
                )
            )
            notifiedPrayerDao.insertNotifiedPrayer(
                NotifiedPrayer(
                    EnumConfig.DHUHR,
                    true,
                    "00:00"
                )
            )
            notifiedPrayerDao.insertNotifiedPrayer(
                NotifiedPrayer(
                    EnumConfig.ASR,
                    true,
                    "00:00"
                )
            )
            notifiedPrayerDao.insertNotifiedPrayer(
                NotifiedPrayer(
                    EnumConfig.MAGHRIB,
                    true,
                    "00:00"
                )
            )
            notifiedPrayerDao.insertNotifiedPrayer(
                NotifiedPrayer(
                    EnumConfig.ISHA,
                    true,
                    "00:00"
                )
            )
            notifiedPrayerDao.insertNotifiedPrayer(
                NotifiedPrayer(
                    EnumConfig.SUNRISE,
                    true,
                    "00:00"
                )
            )
        }

    }

}