package com.programmergabut.solatkuy.di

import android.content.Context
import androidx.room.Room
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModuleAndroidTest {

    @Provides
    @Named("test_db")
    fun provideInMemoryDB(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(context, SolatKuyRoom::class.java)
            .allowMainThreadQueries()
            .build()


}