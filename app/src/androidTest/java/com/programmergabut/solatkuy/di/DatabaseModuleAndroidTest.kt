package com.programmergabut.solatkuy.di

import android.content.Context
import androidx.room.Room
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModuleAndroidTest {

    @Provides
    @Named("test_db")
    fun provideInMemoryDB(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(context, SolatKuyRoom::class.java)
            .allowMainThreadQueries()
            .build()

    /* DAO */
    @Singleton
    @Provides
    @Named("test_notifiedPrayerDao")
    fun provideNotifiedPrayerDaoTesting(@Named("test_db") db: SolatKuyRoom) = db.notifiedPrayerDao()

    @Singleton
    @Provides
    @Named("test_msApi1Dao")
    fun provideMsApi1DaoTesting(@Named("test_db") db: SolatKuyRoom) = db.msApi1Dao()

    @Singleton
    @Provides
    @Named("test_msSettingDao")
    fun provideMsSettingDaoTesting(@Named("test_db") db: SolatKuyRoom) = db.msSettingDao()

    @Singleton
    @Provides
    @Named("test_msFavAyahDao")
    fun provideMsFavAyahDaoTesting(@Named("test_db") db: SolatKuyRoom) = db.msFavAyahDao()

    @Singleton
    @Provides
    @Named("test_msFavSurahDao")
    fun provideMsFavSurahDaoTesting(@Named("test_db") db: SolatKuyRoom) = db.msFavSurahDao()

}