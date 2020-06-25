package com.programmergabut.solatkuy.di.timings.module

import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Data
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Date
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Meta
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Timings
import dagger.Module
import dagger.Provides

/*
 * Created by Katili Jiwo Adi Wiyono on 16/04/20.
 */

@Module
class DataModule(private val timings: Timings, private val day: String, private val en: String, private val number: Int) {

    @Provides
    fun date(): Date = DateModule(
        day,
        en,
        number
    ).provideDate()
    @Provides
    fun meta(): Meta? = null
    @Provides
    fun timings(): Timings = timings


    @Provides
    fun provideData(): Data {
        return Data(
            date(),
            meta(),
            timings()
        )
    }

}