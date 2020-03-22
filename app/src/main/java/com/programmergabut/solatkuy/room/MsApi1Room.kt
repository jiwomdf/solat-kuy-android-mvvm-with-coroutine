package com.programmergabut.solatkuy.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.programmergabut.solatkuy.data.local.MsApi1Dao
import com.programmergabut.solatkuy.data.local.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.model.MsApi1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [MsApi1::class], version = 1, exportSchema = false)
abstract class MsApi1Room: RoomDatabase() {

    abstract fun msApi1Dao(): MsApi1Dao

    companion object{
        @Volatile
        private var INSTANCE: MsApi1Room? = null

        fun getDataBase(context: Context, scope: CoroutineScope): MsApi1Room{
            val tempInstance = INSTANCE

            if(tempInstance != null)
                return tempInstance

            synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    MsApi1Room::class.java,"solatkuydb")
                    .addCallback(MsApi1CallBack(scope)).build()

                INSTANCE = instance
                return instance
            }
        }


        private class MsApi1CallBack(private val scope: CoroutineScope): RoomDatabase.Callback(){

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE.let {
                    scope.launch {
                        populateDatabase(it?.msApi1Dao()!!)
                    }
                }
            }

            suspend fun populateDatabase(msApi1Dao: MsApi1Dao){
                msApi1Dao.deleteAll()
                msApi1Dao.insertMsApi1(MsApi1("-7.55611","110.83167","8","3","2020"))
            }
        }

    }

}