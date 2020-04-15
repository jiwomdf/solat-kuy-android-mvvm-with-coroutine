package com.programmergabut.solatkuy.di.component

import com.programmergabut.solatkuy.data.model.prayerJson.Gregorian
import com.programmergabut.solatkuy.di.module.GregorianModule
import dagger.Component

@Component(modules = [GregorianModule::class])
interface IGregorianComponent {
    fun getGregorian(): Gregorian
}