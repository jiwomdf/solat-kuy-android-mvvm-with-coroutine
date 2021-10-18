package com.programmergabut.solatkuy.di

import com.google.gson.GsonBuilder
import com.programmergabut.solatkuy.BuildConfig
import com.programmergabut.solatkuy.data.remote.api.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val AladhanAPI = "AladhanAPI"
const val QuranAPI = "QuranApi"

val networkModule = module {

    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(GsonBuilder().create())
    }

    fun provideOkHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
        return okHttpClientBuilder.build()
    }


    fun provideRetrofitAladhan(
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_ALADHAN)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }


    fun provideRetrofitQuranApi(
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_QURAN_API)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }


    single { provideGsonConverterFactory() }
    single { provideOkHttpClient() }
    single(named(AladhanAPI)) { provideRetrofitAladhan(get(), get()) }
    single(named(QuranAPI)) { provideRetrofitQuranApi(get(), get()) }

    single { get<Retrofit>(named(AladhanAPI)).create(AsmaAlHusnaService::class.java) }
    single { get<Retrofit>(named(AladhanAPI)).create(PrayerApiService::class.java) }
    single { get<Retrofit>(named(AladhanAPI)).create(QiblaApiService::class.java) }
    single { get<Retrofit>(named(QuranAPI)).create(AllSurahService::class.java) }
    single { get<Retrofit>(named(QuranAPI)).create(ReadSurahArService::class.java) }
    single { get<Retrofit>(named(QuranAPI)).create(ReadSurahEnService::class.java) }

}
