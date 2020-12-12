package com.programmergabut.solatkuy.di

import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.PrayerRepositoryImpl
import com.programmergabut.solatkuy.data.local.dao.MsApi1Dao
import com.programmergabut.solatkuy.data.local.dao.MsSettingDao
import com.programmergabut.solatkuy.data.local.dao.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.remote.RemoteDataSourceAladhanImpl
import com.programmergabut.solatkuy.viewmodel.FakePrayerRepositoryAndroidTest
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

object RepositoryModuleAndroidtest {

    /* @Singleton
    @Provides
    fun provideFakePrayerRepositoryAndroidTest(
        remoteDataSourceAladhan: RemoteDataSourceAladhanImpl,
        notifiedPrayerDao: NotifiedPrayerDao,
        @Named("test_msApi1Dao") msApi1Dao: MsApi1Dao,
        @Named("test_msSettingDao") msSettingDao: MsSettingDao
    ) = FakePrayerRepositoryAndroidTest(remoteDataSourceAladhan, notifiedPrayerDao, msApi1Dao, msSettingDao) as PrayerRepository
       */
}