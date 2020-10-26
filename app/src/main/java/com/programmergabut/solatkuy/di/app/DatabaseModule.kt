package com.programmergabut.solatkuy.di.app

import android.content.Context
import androidx.room.Room
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import com.programmergabut.solatkuy.util.enumclass.EnumConfig.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideSolatKuyDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, SolatKuyRoom::class.java, DATABASE_NAME).build()

    /* DAO */
    @Singleton
    @Provides
    fun provideNotifiedPrayerDao(db: SolatKuyRoom) = db.notifiedPrayerDao()

    @Singleton
    @Provides
    fun provideMsApi1Dao(db: SolatKuyRoom) = db.msApi1Dao()

    @Singleton
    @Provides
    fun provideMsSettingDao(db: SolatKuyRoom) = db.msSettingDao()

    @Singleton
    @Provides
    fun provideMsFavAyahDao(db: SolatKuyRoom) = db.msFavAyahDao()

    @Singleton
    @Provides
    fun provideMsFavSurahDao(db: SolatKuyRoom) = db.msFavSurahDao()

}