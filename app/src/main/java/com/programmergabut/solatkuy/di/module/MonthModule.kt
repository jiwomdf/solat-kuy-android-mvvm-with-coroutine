package com.programmergabut.solatkuy.di.module

import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Month
import dagger.Module
import dagger.Provides

/*
 * Created by Katili Jiwo Adi Wiyono on 16/04/20.
 */

@Module
class MonthModule(private val en: String, private val number: Int) {

    @Provides
    fun en(): String = en
    @Provides
    fun number(): Int = number

    @Provides
    fun provideMonth(): Month {
        return Month(
            en(),
            number()
        )
    }

}