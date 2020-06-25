package com.programmergabut.solatkuy.di.app

import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquran
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object rdsApiAlquranModule {

    @Singleton
    @Provides
    fun provideRdsAladhan(): RemoteDataSourceApiAlquran = RemoteDataSourceApiAlquran()

}