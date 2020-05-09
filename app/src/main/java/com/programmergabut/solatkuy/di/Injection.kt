package com.programmergabut.solatkuy.di

import android.app.Application
import com.programmergabut.solatkuy.data.RemoteDataSource
import com.programmergabut.solatkuy.data.repository.Repository
import kotlinx.coroutines.CoroutineScope

object Injection {

    fun provideRepository(application: Application, scope: CoroutineScope): Repository {
        val remoteDataSource = RemoteDataSource.getInstance()

        return Repository(application, scope, remoteDataSource)
    }

}