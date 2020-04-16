package com.programmergabut.solatkuy.di.component

import com.programmergabut.solatkuy.data.model.prayerJson.Month
import com.programmergabut.solatkuy.di.module.GregorianModule
import com.programmergabut.solatkuy.di.module.MonthModule
import dagger.Component

@Component(modules = [MonthModule::class])
interface IMonthComponent {
    fun getMonth(): Month
}