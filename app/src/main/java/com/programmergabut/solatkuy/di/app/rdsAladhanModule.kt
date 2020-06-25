package com.programmergabut.solatkuy.di.app

import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhan
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object rdsAladhanModule {

    @Singleton
    @Provides
    fun provideRdsAladhan(): RemoteDataSourceAladhan = RemoteDataSourceAladhan()

}