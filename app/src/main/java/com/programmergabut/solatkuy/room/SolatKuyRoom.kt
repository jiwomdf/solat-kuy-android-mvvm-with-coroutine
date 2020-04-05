package com.programmergabut.solatkuy.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.programmergabut.solatkuy.data.local.MsApi1Dao
import com.programmergabut.solatkuy.data.local.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.data.model.entity.PrayerLocal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(version = 4, entities = [PrayerLocal::class, MsApi1::class])
abstract class SolatKuyRoom: RoomDatabase() {

    abstract fun notifiedPrayerDao(): NotifiedPrayerDao
    abstract fun msApi1Dao(): MsApi1Dao

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
                    //.fallbackToDestructiveMigration()
                    /*.addMigrations(object :Migration(1,2){
                        override fun migrate(database: SupportSQLiteDatabase) {
                            database.execSQL("CREATE TABLE MsApi1 (`api1ID` INTEGER, "
                                    + "`latitude` TEXT, "
                                    + "`longitude` TEXT, "
                                    + "`method` TEXT, "
                                    + "`month` TEXT"
                                    + "`year` TEXT"
                                    + "PRIMARY KEY(`id`))")

                            //var api1ID: Int, val latitude: String, val longitude: String, val method: String, val month: String, val year: String
                        }

                  })*/
                    .addCallback(NotifiedPrayerCallBack(scope))
                    .build()

                INSTANCE = instance
                return instance
            }
        }

        fun getDataBase(context: Context): SolatKuyRoom{
            val tempInstance = INSTANCE

            if(tempInstance != null)
                return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    SolatKuyRoom::class.java,"solatkuydb")
                    .build()

                INSTANCE = instance
                return instance
            }
        }

        private class NotifiedPrayerCallBack(private val scope: CoroutineScope): RoomDatabase.Callback(){

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                INSTANCE.let {
                    scope.launch {
                        populateNotifiedPrayer(it?.notifiedPrayerDao()!!)
                        populateMsApi1(it.msApi1Dao())
                    }
                }

            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
            }

            suspend fun populateMsApi1(msApi1Dao: MsApi1Dao) {
                msApi1Dao.deleteAll()

                msApi1Dao.insertMsApi1(
                    MsApi1(
                        1,
                        "-7.5755",
                        "110.8243",
                        "8",
                        "3",
                        "2020"
                    )
                )
            }

            suspend fun populateNotifiedPrayer(notifiedPrayerDao: NotifiedPrayerDao){
                notifiedPrayerDao.deleteAll()

                notifiedPrayerDao.insertNotifiedPrayer(
                    PrayerLocal(
                        "Fajr",
                        true,
                        "00:00"
                    )
                )
                notifiedPrayerDao.insertNotifiedPrayer(
                    PrayerLocal(
                        "Dhuhr",
                        true,
                        "00:00"
                    )
                )
                notifiedPrayerDao.insertNotifiedPrayer(
                    PrayerLocal(
                        "Asr",
                        true,
                        "00:00"
                    )
                )
                notifiedPrayerDao.insertNotifiedPrayer(
                    PrayerLocal(
                        "Maghrib",
                        true,
                        "00:00"
                    )
                )
                notifiedPrayerDao.insertNotifiedPrayer(
                    PrayerLocal(
                        "Isha",
                        true,
                        "00:00"
                    )
                )
                notifiedPrayerDao.insertNotifiedPrayer(
                    PrayerLocal(
                        "sunrise",
                        true,
                        "00:00"
                    )
                )
            }
        }


    }

}