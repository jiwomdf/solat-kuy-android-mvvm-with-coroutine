package com.programmergabut.solatkuy.di

import android.app.Application
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhan
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.LocalDataSource
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceApiAlquran
import kotlinx.coroutines.GlobalScope

object Injection {

    fun provideRepository(application: Application): Repository {

        val db: SolatKuyRoom = SolatKuyRoom.getDataBase(application)

        val remoteDataSourceAladhan = RemoteDataSourceAladhan.getInstance()
        val remoteDataSourceQuranApi = RemoteDataSourceApiAlquran.getInstance()
        //val localDataSource = LocalDataSource.getInstance(db)


        return Repository.getInstance(
            remoteDataSourceAladhan,
            remoteDataSourceQuranApi,
            db.notifiedPrayerDao(),
            db.msApi1Dao(),
            db.msSettingDao(),
            db.msFavAyahDao(),
            db.msFavSurahDao()
        )
    }

}