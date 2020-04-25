package com.programmergabut.solatkuy.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.programmergabut.solatkuy.data.local.MsApi1Dao
import com.programmergabut.solatkuy.data.local.MsSettingDao
import com.programmergabut.solatkuy.data.local.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.data.model.entity.MsSetting
import com.programmergabut.solatkuy.data.model.entity.PrayerLocal
import com.programmergabut.solatkuy.util.EnumPrayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

@Database(version = 5, entities = [PrayerLocal::class, MsApi1::class, MsSetting::class])
abstract class SolatKuyRoom: RoomDatabase() {

    abstract fun notifiedPrayerDao(): NotifiedPrayerDao
    abstract fun msApi1Dao(): MsApi1Dao
    abstract fun msSettingDao(): MsSettingDao


    companion object{
        @Volatile
        private var INSTANCE: SolatKuyRoom? = null
        const val dbName = "solatkuydb"

        fun getDataBase(context: Context, scope: CoroutineScope): SolatKuyRoom{
            val tempInstance = INSTANCE

            if(tempInstance != null)
                return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, SolatKuyRoom::class.java, dbName)
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
                    SolatKuyRoom::class.java, dbName)
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
                        it?.let {
                            populateMsSetting(it.msSettingDao())
                            populateNotifiedPrayer(it.notifiedPrayerDao())
                            populateMsApi1(it.msApi1Dao())
                        }
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)

            }

            suspend fun populateMsSetting(msSettingDao: MsSettingDao){
                msSettingDao.deleteAll()

                msSettingDao.insertMsSetting(
                    MsSetting(1, false)
                )
            }

            suspend fun populateMsApi1(msApi1Dao: MsApi1Dao) {
                msApi1Dao.deleteAll()

                msApi1Dao.insertMsApi1(
                    MsApi1(
                        1,
                        "0",
                        "0",
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
                        EnumPrayer.fajr,
                        true,
                        "00:00"
                    )
                )
                notifiedPrayerDao.insertNotifiedPrayer(
                    PrayerLocal(
                        EnumPrayer.dhuhr,
                        true,
                        "00:00"
                    )
                )
                notifiedPrayerDao.insertNotifiedPrayer(
                    PrayerLocal(
                        EnumPrayer.asr,
                        true,
                        "00:00"
                    )
                )
                notifiedPrayerDao.insertNotifiedPrayer(
                    PrayerLocal(
                        EnumPrayer.maghrib,
                        true,
                        "00:00"
                    )
                )
                notifiedPrayerDao.insertNotifiedPrayer(
                    PrayerLocal(
                        EnumPrayer.isha,
                        true,
                        "00:00"
                    )
                )
                notifiedPrayerDao.insertNotifiedPrayer(
                    PrayerLocal(
                        EnumPrayer.sunrise,
                        true,
                        "00:00"
                    )
                )
            }
        }


    }

}