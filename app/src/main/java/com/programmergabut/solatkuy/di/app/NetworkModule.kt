package com.programmergabut.solatkuy.di.app

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.programmergabut.solatkuy.BuildConfig
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhan
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhanImpl
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquran
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquranImpl
import com.programmergabut.solatkuy.data.remote.api.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    /* Services */
    @AladhanEndPoint
    @Provides
    @Singleton
    fun provideAladhanEndPoint() = BuildConfig.BASE_URL_ALADHAN

    @QuranApiEndPoint
    @Provides
    @Singleton
    fun provideQuranApiEndPoint() = BuildConfig.BASE_URL_QURAN_API

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(GsonBuilder().create())
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    @AladhanApi
    @Provides
    @Singleton
    fun provideRetrofitAladhan(@AladhanEndPoint BASE_URL: String, gsonConverterFactory: GsonConverterFactory,
                               okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @QuranApi
    @Provides
    @Singleton
    fun provideRetrofitQuranApi(@QuranApiEndPoint BASE_URL: String, gsonConverterFactory: GsonConverterFactory,
                                okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideAsmaAlHusnaService(@AladhanApi retrofit: Retrofit): AsmaAlHusnaService = retrofit.create(
        AsmaAlHusnaService::class.java)

    @Provides
    @Singleton
    fun provideCalendarApiService(@AladhanApi retrofit: Retrofit): CalendarApiService = retrofit.create(
        CalendarApiService::class.java)

    @Provides
    @Singleton
    fun provideQiblaApiService(@AladhanApi retrofit: Retrofit): QiblaApiService = retrofit.create(
        QiblaApiService::class.java)

    @Provides
    @Singleton
    fun provideAllSurahService(@QuranApi retrofit: Retrofit): AllSurahService = retrofit.create(
        AllSurahService::class.java)

    @Provides
    @Singleton
    fun provideReadSurahArService(@QuranApi retrofit: Retrofit): ReadSurahArService = retrofit.create(
        ReadSurahArService::class.java)

    @Provides
    @Singleton
    fun provideReadSurahEnService(@QuranApi retrofit: Retrofit): ReadSurahEnService = retrofit.create(
        ReadSurahEnService::class.java)

    @Provides
    @Singleton
    fun provideRemoteDataSourceAladhan(
        qiblaApiService: QiblaApiService, calendarApiService: CalendarApiService
    ) = RemoteDataSourceAladhanImpl(qiblaApiService, calendarApiService) as RemoteDataSourceAladhan

    @Provides
    @Singleton
    fun provideRemoteDataSourceApiAlquran(
        readSurahEnService: ReadSurahEnService,
        allSurahService: AllSurahService,
        readSurahArService: ReadSurahArService
    ) = RemoteDataSourceApiAlquranImpl(
        readSurahEnService,
        allSurahService,
        readSurahArService) as RemoteDataSourceApiAlquran

}