package com.programmergabut.solatkuy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.util.EnumConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.room.Room

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

        @Volatile
        private var INSTANCE: SolatKuyRoom? = null
        private const val dbName = EnumConfig.DATABASE_NAME

        fun getDataBase(context: Context): SolatKuyRoom {
            val tempInstance = INSTANCE

            if(tempInstance != null)
                return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, SolatKuyRoom::class.java, dbName)
                    .allowMainThreadQueries()
                    .build()

                INSTANCE = instance
                return instance
            }
        }

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
                    EnumConfig.START_LAT,
                    EnumConfig.START_LNG,
                    EnumConfig.START_METHOD,
                    EnumConfig.START_MONTH,
                    EnumConfig.START_YEAR
                )
            )
        }

        private suspend fun populateNotifiedPrayer(notifiedPrayerDao: NotifiedPrayerDao){
            notifiedPrayerDao.deleteAll()

            notifiedPrayerDao.insertNotifiedPrayer(
                NotifiedPrayer(
                    EnumConfig.FAJR,
                    true,
                    EnumConfig.FAJR_TIME
                )
            )
            notifiedPrayerDao.insertNotifiedPrayer(
                NotifiedPrayer(
                    EnumConfig.DHUHR,
                    true,
                    EnumConfig.DHUHR_TIME
                )
            )
            notifiedPrayerDao.insertNotifiedPrayer(
                NotifiedPrayer(
                    EnumConfig.ASR,
                    true,
                    EnumConfig.ASR_TIME
                )
            )
            notifiedPrayerDao.insertNotifiedPrayer(
                NotifiedPrayer(
                    EnumConfig.MAGHRIB,
                    true,
                    EnumConfig.MAGHRIB_TIME
                )
            )
            notifiedPrayerDao.insertNotifiedPrayer(
                NotifiedPrayer(
                    EnumConfig.ISHA,
                    true,
                    EnumConfig.ISHA_TIME
                )
            )
            notifiedPrayerDao.insertNotifiedPrayer(
                NotifiedPrayer(
                    EnumConfig.SUNRISE,
                    true,
                    EnumConfig.SUNRISE_TIME
                )
            )
        }

    }

}