package com.programmergabut.solatkuy.di.component

import com.programmergabut.solatkuy.data.model.prayerJson.Data
import com.programmergabut.solatkuy.di.module.DataModule
import dagger.Component

@Component(modules = [DataModule::class])
interface IDataComponent {
    fun getData(): Data
}