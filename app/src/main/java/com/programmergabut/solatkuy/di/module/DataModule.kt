package com.programmergabut.solatkuy.di.module

import com.google.gson.annotations.SerializedName
import com.programmergabut.solatkuy.data.model.prayerJson.Data
import com.programmergabut.solatkuy.data.model.prayerJson.Date
import com.programmergabut.solatkuy.data.model.prayerJson.Meta
import com.programmergabut.solatkuy.data.model.prayerJson.Timings
import dagger.Module
import dagger.Provides

@Module
class DataModule(private val timings: Timings,private val day: String,  private val en: String, private val number: Int) {

    @Provides
    fun date(): Date = DateModule(day, en, number).provideDate()
    @Provides
    fun meta(): Meta? = null
    @Provides
    fun timings(): Timings = timings


    @Provides
    fun provideData(): Data {
        return Data(date(), meta(), timings())
    }

}