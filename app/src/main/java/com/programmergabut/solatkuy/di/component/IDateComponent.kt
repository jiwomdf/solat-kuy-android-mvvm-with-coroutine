package com.programmergabut.solatkuy.di.component

import com.programmergabut.solatkuy.data.model.prayerJson.Date
import com.programmergabut.solatkuy.data.model.prayerJson.Gregorian
import com.programmergabut.solatkuy.di.module.DateModule
import com.programmergabut.solatkuy.di.module.GregorianModule
import dagger.Component

@Component(modules = [DateModule::class])
interface IDateComponent {
    fun getDate(): Date
}