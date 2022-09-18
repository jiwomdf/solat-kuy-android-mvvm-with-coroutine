package com.programmergabut.solatkuy.di

import android.content.SharedPreferences
import com.programmergabut.solatkuy.data.repository.QuranRepository
import com.programmergabut.solatkuy.ui.SolatKuyFragmentFactory
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SubModuleDependencies {

    fun provideSolatKuyFragmentFactory(): SolatKuyFragmentFactory
    fun provideQuran(): QuranRepository
    fun provideSharedPreferences(): SharedPreferences
    fun provideGsonConverterFactory(): GsonConverterFactory
    fun provideOkHttpClient(): OkHttpClient
}