package com.programmergabut.solatkuy.di

/* object Injection {

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

} */