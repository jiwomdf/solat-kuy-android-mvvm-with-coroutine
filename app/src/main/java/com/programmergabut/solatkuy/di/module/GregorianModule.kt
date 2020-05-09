package com.programmergabut.solatkuy.di.module

import com.programmergabut.solatkuy.data.model.prayerJson.Gregorian
import com.programmergabut.solatkuy.data.model.prayerJson.Month
import dagger.Module
import dagger.Provides

/*
 * Created by Katili Jiwo Adi Wiyono on 16/04/20.
 */

@Module
class GregorianModule(private val day: String, private val en: String, private val number: Int) {

    @Provides
    fun date() = null
    @Provides
    fun day(): String = day
    @Provides
    fun designation() = null
    @Provides
    fun format() = null
    @Provides
    fun month(): Month? = MonthModule(en, number).provideMonth()
    @Provides
    fun weekday() = null
    @Provides
    fun year() = null


    @Provides
    fun provideGregorian(): Gregorian {
        return Gregorian(date(), day, designation(), format(), month(), weekday(), year())
    }

}