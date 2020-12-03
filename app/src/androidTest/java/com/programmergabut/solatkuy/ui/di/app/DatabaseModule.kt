package com.programmergabut.solatkuy.ui.di.app

import android.content.Context
import androidx.room.Room
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named


@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {
    @Provides
    @Named("test_db")
    fun provideInMemoryDB(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(context, SolatKuyRoom::class.java)
            .allowMainThreadQueries()
            .build()
}