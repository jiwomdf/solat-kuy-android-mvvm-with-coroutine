package com.programmergabut.solatkuy.di

import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.PrayerRepositoryImpl
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.QuranRepositoryImpl
import com.programmergabut.solatkuy.data.local.dao.*
import com.programmergabut.solatkuy.data.remote.api.*
import com.programmergabut.solatkuy.util.ContextProviders
import org.koin.dsl.module


val repositoryModule = module {


    fun provideContextProvider(): ContextProviders = ContextProviders.getInstance()


    fun providePrayerRepositoryImpl(
        msNotifiedPrayerDao: MsNotifiedPrayerDao,
        msApi1Dao: MsApi1Dao,
        msSettingDao: MsSettingDao,
        msCalculationMethodsDao: MsCalculationMethodsDao,
        contextProviders: ContextProviders,
        qiblaApiService: QiblaApiService,
        prayerApiService: PrayerApiService
    ) = PrayerRepositoryImpl(
        msNotifiedPrayerDao,
        msApi1Dao,
        msSettingDao,
        msCalculationMethodsDao,
        contextProviders,
        qiblaApiService,
        prayerApiService
    ) as PrayerRepository


    fun provideQuranRepositoryImpl(
        msFavSurahDao: MsFavSurahDao,
        msSurahDao: MsSurahDao,
        msAyahDao: MsAyahDao,
        readSurahEnService: ReadSurahEnService,
        allSurahService: AllSurahService,
        readSurahArService: ReadSurahArService,
        contextProviders: ContextProviders
    ) = QuranRepositoryImpl(
        msFavSurahDao,
        msSurahDao,
        msAyahDao,
        readSurahEnService,
        allSurahService,
        readSurahArService,
        contextProviders,
    ) as QuranRepository

    single { provideContextProvider() }
    single { providePrayerRepositoryImpl(get(), get(), get(), get(), get(), get(), get()) }
    single { provideQuranRepositoryImpl(get(), get(), get(), get(), get(), get(), get()) }
}