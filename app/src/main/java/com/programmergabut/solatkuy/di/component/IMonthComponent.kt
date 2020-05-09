package com.programmergabut.solatkuy.di.component

import com.programmergabut.solatkuy.data.model.prayerJson.Month
import com.programmergabut.solatkuy.di.module.MonthModule
import dagger.Component

/*
 * Created by Katili Jiwo Adi Wiyono on 16/04/20.
 */

@Component(modules = [MonthModule::class])
interface IMonthComponent {
    fun getMonth(): Month
}