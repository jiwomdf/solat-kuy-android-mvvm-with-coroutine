package com.programmergabut.solatkuy.di

import android.app.Application
import androidx.room.Room
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.util.Constant
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


val databaseModule = module {

    fun provideDatabase(application: Application): SolatKuyRoom {
        return Room.databaseBuilder(application, SolatKuyRoom::class.java, Constant.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideNotifiedPrayerDao(database: SolatKuyRoom): MsNotifiedPrayerDao {
        return database.notifiedPrayerDao
    }

    fun provideMsApi1Dao(database: SolatKuyRoom): MsApi1Dao {
        return database.msApi1Dao
    }

    fun provideMsSettingDao(database: SolatKuyRoom): MsSettingDao {
        return database.msSettingDao
    }

    fun provideMsFavSurahDao(database: SolatKuyRoom): MsFavSurahDao {
        return database.msFavSurahDao
    }

    fun provideMsSurahDao(database: SolatKuyRoom): MsSurahDao {
        return database.msSurahDao
    }

    fun provideMsAyahDao(database: SolatKuyRoom): MsAyahDao {
        return database.msAyahDao
    }

    fun provideMsCalculationMethodsDao(database: SolatKuyRoom): MsCalculationMethodsDao {
        return database.msCalculationMethodsDao
    }

    single { provideDatabase(androidApplication()) }

    single { provideNotifiedPrayerDao(get()) }
    single { provideMsApi1Dao(get()) }
    single { provideMsSettingDao(get()) }
    single { provideMsFavSurahDao(get()) }
    single { provideMsSurahDao(get()) }
    single { provideMsAyahDao(get()) }
    single { provideMsCalculationMethodsDao(get()) }


}




