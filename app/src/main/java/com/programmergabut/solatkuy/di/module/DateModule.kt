package com.programmergabut.solatkuy.di.module

import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.data.model.prayerJson.Date
import com.programmergabut.solatkuy.data.model.prayerJson.Gregorian
import com.programmergabut.solatkuy.data.model.prayerJson.Hijri
import com.programmergabut.solatkuy.data.model.prayerJson.Month
import dagger.Module
import dagger.Provides

@Module
class DateModule(private val gregorian: Gregorian) {

    @Provides
    fun gregorian(): Gregorian = gregorian
    @Provides
    fun hijri(): Hijri? = null
    @Provides
    fun readable(): String? = null
    @Provides
    fun timestamp(): String? = null


    @Provides
    fun provideDate(): Date {
        return Date(gregorian, null, null, null)
    }

}