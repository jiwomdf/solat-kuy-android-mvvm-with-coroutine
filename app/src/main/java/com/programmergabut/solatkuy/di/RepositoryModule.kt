package com.programmergabut.solatkuy.di

import com.programmergabut.solatkuy.data.repository.PrayerRepository
import com.programmergabut.solatkuy.data.repository.PrayerRepositoryImpl
import com.programmergabut.solatkuy.data.repository.QuranRepository
import com.programmergabut.solatkuy.data.repository.QuranRepositoryImpl
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.remote.api.*
import com.programmergabut.solatkuy.di.contextprovider.ContextProvider
import com.programmergabut.solatkuy.di.contextprovider.ContextProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideContextProvider(): ContextProvider = ContextProviderImpl()

    @Singleton
    @Provides
    fun providePrayerRepositoryImpl(
        msNotifiedPrayerDao: MsNotifiedPrayerDao,
        msConfigurationDao: MsConfigurationDao,
        msSettingDao: MsSettingDao,
        msCalculationMethodsDao: MsCalculationMethodsDao,
        contextProvider: ContextProvider,
        qiblaApiService: QiblaApiService,
        prayerApiService: PrayerApiService
    ) = PrayerRepositoryImpl(msNotifiedPrayerDao,
        msConfigurationDao, msSettingDao, msCalculationMethodsDao, contextProvider, qiblaApiService, prayerApiService) as PrayerRepository

    @Singleton
    @Provides
    fun provideQuranRepositoryImpl(
        msFavSurahDao: MsFavSurahDao,
        msSurahDao: MsSurahDao,
        msAyahDao: MsAyahDao,
        fastRataDao: FastRataDao,
        readSurahEnService: ReadSurahEnService,
        allSurahService: AllSurahService,
        readSurahArService: ReadSurahArService,
        contextProvider: ContextProvider
    ) = QuranRepositoryImpl(
        fastRataDao,
        msFavSurahDao,
        msSurahDao,
        msAyahDao,
        readSurahEnService,
        allSurahService,
        readSurahArService,
        contextProvider
    ) as QuranRepository


}