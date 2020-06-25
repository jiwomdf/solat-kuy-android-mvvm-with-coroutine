package com.programmergabut.solatkuy.di.timings.module

import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Date
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Gregorian
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Hijri
import dagger.Module
import dagger.Provides

/*
 * Created by Katili Jiwo Adi Wiyono on 16/04/20.
 */

@Module
class DateModule(private val day: String,  private val en: String, private val number: Int) {

    @Provides
    fun gregorian(): Gregorian = GregorianModule(
        day,
        en,
        number
    ).provideGregorian()

    @Provides
    fun hijri(): Hijri? = null
    @Provides
    fun readable(): String? = null
    @Provides
    fun timestamp(): String? = null


    @Provides
    fun provideDate(): Date {
        return Date(
            gregorian(),
            hijri(),
            readable(),
            timestamp()
        )
    }

}