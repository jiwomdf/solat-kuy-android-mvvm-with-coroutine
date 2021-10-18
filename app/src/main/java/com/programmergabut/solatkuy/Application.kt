package com.programmergabut.solatkuy

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.work.Configuration
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.di.*
import com.programmergabut.solatkuy.worker.MyWorkerFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level



class Application : Application(), Configuration.Provider {


    lateinit var db: SolatKuyRoom

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@Application)
            fragmentFactory()
            modules(
                listOf(
                    databaseModule, networkModule, repositoryModule, sharedPrefModule,
                    viewModelModule
                )
            )

        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
    }


}

