package com.programmergabut.solatkuy.di.component

import com.programmergabut.solatkuy.data.model.prayerJson.Date
import com.programmergabut.solatkuy.di.module.DateModule
import dagger.Component

@Component(modules = [DateModule::class])
interface IDateComponent {
    fun getDate(): Date
}