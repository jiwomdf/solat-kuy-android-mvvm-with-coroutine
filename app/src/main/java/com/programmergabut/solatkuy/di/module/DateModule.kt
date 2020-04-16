package com.programmergabut.solatkuy.di.module

import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.data.model.prayerJson.Date
import com.programmergabut.solatkuy.data.model.prayerJson.Gregorian
import com.programmergabut.solatkuy.data.model.prayerJson.Hijri
import com.programmergabut.solatkuy.data.model.prayerJson.Month
import dagger.Module
import dagger.Provides

@Module
class DateModule(private val day: String,  private val en: String, private val number: Int) {

    @Provides
    fun gregorian(): Gregorian = GregorianModule(day, en, number).provideGregorian()

    @Provides
    fun hijri(): Hijri? = null
    @Provides
    fun readable(): String? = null
    @Provides
    fun timestamp(): String? = null


    @Provides
    fun provideDate(): Date {
        return Date(gregorian(), hijri(), readable(), timestamp())
    }

}