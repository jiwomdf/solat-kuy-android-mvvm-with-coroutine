package com.programmergabut.solatkuy.di.app

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhan
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquran
import com.programmergabut.solatkuy.data.remote.api.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    /* Services */
    @AladhanEndPoint
    @Provides
    @Singleton
    fun provideAladhanEndPoint() = "https://api.aladhan.com/v1/"

    @QuranApiEndPoint
    @Provides
    @Singleton
    fun provideQuranApiEndPoint() = "https://api.alquran.cloud/v1/"

    @Provides
    @Singleton
    fun provideGsonBuilder(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @AladhanApi
    @Provides
    @Singleton
    fun provideRetrofitAladhan(@AladhanEndPoint BASE_URL: String, gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @QuranApi
    @Provides
    @Singleton
    fun provideRetrofitQuranApi(@QuranApiEndPoint BASE_URL: String, gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    //@AladhanApi
    @Provides
    @Singleton
    fun provideAsmaAlHusnaService(@AladhanApi retrofit: Retrofit): AsmaAlHusnaService = retrofit.create(
        AsmaAlHusnaService::class.java)

    //@AladhanApi
    @Provides
    @Singleton
    fun provideCalendarApiService(@AladhanApi retrofit: Retrofit): CalendarApiService = retrofit.create(
        CalendarApiService::class.java)

    //@AladhanApi
    @Provides
    @Singleton
    fun provideQiblaApiService(@AladhanApi retrofit: Retrofit): QiblaApiService = retrofit.create(
        QiblaApiService::class.java)

    //@QuranApi
    @Provides
    @Singleton
    fun provideAllSurahService(@QuranApi retrofit: Retrofit): AllSurahService = retrofit.create(
        AllSurahService::class.java)

    //@QuranApi
    @Provides
    @Singleton
    fun provideReadSurahArService(@QuranApi retrofit: Retrofit): ReadSurahArService = retrofit.create(
        ReadSurahArService::class.java)

    //@QuranApi
    @Provides
    @Singleton
    fun provideReadSurahEnService(@QuranApi retrofit: Retrofit): ReadSurahEnService = retrofit.create(
        ReadSurahEnService::class.java)

    @Provides
    @Singleton
    fun provideRemoteDataSourceAladhan(remoteDataSourceAladhan: RemoteDataSourceAladhan): RemoteDataSourceAladhan = remoteDataSourceAladhan

    @Provides
    @Singleton
    fun provideRemoteDataSourceApiAlquran(remoteDataSourceApiAlquran: RemoteDataSourceApiAlquran): RemoteDataSourceApiAlquran = remoteDataSourceApiAlquran


}