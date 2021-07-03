package com.programmergabut.solatkuy.di

import com.google.gson.GsonBuilder
import com.programmergabut.solatkuy.BuildConfig
import com.programmergabut.solatkuy.data.remote.api.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /* Services */
    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(GsonBuilder().create())
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .readTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    @AladhanApi
    @Provides
    @Singleton
    fun provideRetrofitAladhan(gsonConverterFactory: GsonConverterFactory,
                               okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_ALADHAN)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @QuranApi
    @Provides
    @Singleton
    fun provideRetrofitQuranApi(gsonConverterFactory: GsonConverterFactory,
                                okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_QURAN_API)
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
    fun provideCalendarApiService(@AladhanApi retrofit: Retrofit): PrayerApiService = retrofit.create(
        PrayerApiService::class.java)

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
}