package com.programmergabut.solatkuy.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.programmergabut.solatkuy.data.local.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.model.PrayerLocal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [PrayerLocal::class], version = 1, exportSchema = false)
abstract class SolatKuyRoom: RoomDatabase() {

    abstract fun notifiedPrayerDao(): NotifiedPrayerDao

    companion object{
        @Volatile
        private var INSTANCE: SolatKuyRoom? = null

        fun getDataBase(context: Context, scope: CoroutineScope): SolatKuyRoom{
            val tempInstance = INSTANCE

            if(tempInstance != null)
                return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                        SolatKuyRoom::class.java,"solatkuydb")
                    .addCallback(NotifiedPrayerCallBack(scope)).build()

                INSTANCE = instance
                return instance
            }
        }

        private class NotifiedPrayerCallBack(private val scope: CoroutineScope): RoomDatabase.Callback(){

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE.let {
                    scope.launch {
                        populateDatabase(it?.notifiedPrayerDao()!!)
                    }
                }
            }

            suspend fun populateDatabase(notifiedPrayerDao: NotifiedPrayerDao){
                notifiedPrayerDao.deleteAll()

                notifiedPrayerDao.insertNotifiedPrayer(PrayerLocal("Fajr",true))
                notifiedPrayerDao.insertNotifiedPrayer(PrayerLocal("Dhuhr",true))
                notifiedPrayerDao.insertNotifiedPrayer(PrayerLocal("Asr",true))
                notifiedPrayerDao.insertNotifiedPrayer(PrayerLocal("Maghrib",true))
                notifiedPrayerDao.insertNotifiedPrayer(PrayerLocal("Isha",true))
            }

        }
    }

}