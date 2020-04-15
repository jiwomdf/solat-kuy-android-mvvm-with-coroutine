package com.programmergabut.solatkuy.di.module

import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.data.model.prayerJson.Date
import com.programmergabut.solatkuy.data.model.prayerJson.Gregorian
import com.programmergabut.solatkuy.data.model.prayerJson.Hijri
import com.programmergabut.solatkuy.data.model.prayerJson.Month
import dagger.Module
import dagger.Provides

@Module
class GregorianModule(private val day: String, private val month: Month) {

    @Provides
    fun date() = null
    @Provides
    fun day(): String = day
    @Provides
    fun designation() = null
    @Provides
    fun format() = null
    @Provides
    fun month(): Month? = month
    @Provides
    fun weekday() = null
    @Provides
    fun year() = null


    @Provides
    fun provideGregorian(): Gregorian {
        return Gregorian(null, day, null, null, month, null, null)
    }

}