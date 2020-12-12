package com.programmergabut.solatkuy.di.app

import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.PrayerRepositoryImpl
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.QuranRepositoryImpl
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhanImpl
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquranImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providePrayerRepositoryImpl(
        remoteDataSourceAladhan: RemoteDataSourceAladhanImpl,
        notifiedPrayerDao: NotifiedPrayerDao,
        msApi1Dao: MsApi1Dao,
        msSettingDao: MsSettingDao
    ) = PrayerRepositoryImpl(remoteDataSourceAladhan, notifiedPrayerDao, msApi1Dao, msSettingDao) as PrayerRepository

    @Singleton
    @Provides
    fun provideQuranRepositoryImpl(
        remoteDataSourceApiAlquranImpl: RemoteDataSourceApiAlquranImpl,
        msFavAyahDao: MsFavAyahDao,
        msFavSurahDao: MsFavSurahDao
    ) = QuranRepositoryImpl(remoteDataSourceApiAlquranImpl, msFavAyahDao, msFavSurahDao) as QuranRepository


}