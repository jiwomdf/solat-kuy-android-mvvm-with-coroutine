package com.programmergabut.solatkuy.di

import android.app.Application
import com.programmergabut.solatkuy.data.ContextProviders
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhan
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.LocalDataSource
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquran

object Injection {

    fun provideRepository(application: Application): Repository {

        val db: SolatKuyRoom = SolatKuyRoom.getDataBase(application)

        val contextProviders = ContextProviders.getInstance()
        val remoteDataSourceAladhan = RemoteDataSourceAladhan.getInstance(contextProviders)
        val remoteDataSourceQuranApi = RemoteDataSourceApiAlquran.getInstance(contextProviders)
        val localDataSource = LocalDataSource.getInstance(contextProviders, db)


        return Repository(
            contextProviders,
            remoteDataSourceAladhan,
            remoteDataSourceQuranApi,
            localDataSource
        )
    }

}