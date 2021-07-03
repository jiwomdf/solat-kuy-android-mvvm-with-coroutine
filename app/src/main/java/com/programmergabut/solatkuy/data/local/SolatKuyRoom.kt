package com.programmergabut.solatkuy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.local.localentity.*
import com.programmergabut.solatkuy.util.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.room.Room

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

@Database(version = 3, entities = [MsNotifiedPrayer::class, MsApi1::class, MsSetting::class, MsFavSurah::class, MsSurah::class, MsAyah::class, MsCalculationMethods::class])
abstract class SolatKuyRoom : RoomDatabase() {

    abstract fun notifiedPrayerDao(): MsNotifiedPrayerDao
    abstract fun msApi1Dao(): MsApi1Dao
    abstract fun msSettingDao(): MsSettingDao
    abstract fun msSurahDao(): MsSurahDao
    abstract fun msAyahDao(): MsAyahDao
    abstract fun msFavSurahDao(): MsFavSurahDao
    abstract fun msCalculationMethodsDao(): MsCalculationMethodsDao

    companion object{
        @Volatile
        private var INSTANCE: SolatKuyRoom? = null
        private const val dbName = Constant.DATABASE_NAME

        fun getDataBase(context: Context): SolatKuyRoom {
            val tempInstance = INSTANCE
            if(tempInstance != null)
                return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, SolatKuyRoom::class.java, dbName)
                    .fallbackToDestructiveMigration()
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
            msSettingDao.insertMsSetting(MsSetting(1, false))
        }

        private suspend fun populateMsApi1(msApi1Dao: MsApi1Dao) {
            msApi1Dao.deleteAll()
            msApi1Dao.insertMsApi1(
                MsApi1(1, Constant.START_LAT, Constant.START_LNG,
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