package com.programmergabut.solatkuy.di.app

import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.PrayerRepositoryImpl
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.QuranRepositoryImpl
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.remote.api.*
import com.programmergabut.solatkuy.util.ContextProviders
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
    fun provideContextProvider(): ContextProviders = ContextProviders.getInstance()

    @Singleton
    @Provides
    fun providePrayerRepositoryImpl(
        notifiedPrayerDao: NotifiedPrayerDao,
        msApi1Dao: MsApi1Dao,
        msSettingDao: MsSettingDao,
        contextProviders: ContextProviders,
        qiblaApiService: QiblaApiService,
        prayerApiService: PrayerApiService
    ) = PrayerRepositoryImpl(notifiedPrayerDao,
        msApi1Dao, msSettingDao,contextProviders, qiblaApiService, prayerApiService) as PrayerRepository

    @Singleton
    @Provides
    fun provideQuranRepositoryImpl(
        msFavAyahDao: MsFavAyahDao,
        msFavSurahDao: MsFavSurahDao,
        msSurahDao: MsSurahDao,
        msAyahDao: MsAyahDao,
        readSurahEnService: ReadSurahEnService,
        allSurahService: AllSurahService,
        readSurahArService: ReadSurahArService,
        contextProviders: ContextProviders
    ) = QuranRepositoryImpl(
        msFavAyahDao,
        msFavSurahDao,
        msSurahDao,
        msAyahDao,
        readSurahEnService,
        allSurahService,
        readSurahArService,
        contextProviders,
    ) as QuranRepository


}