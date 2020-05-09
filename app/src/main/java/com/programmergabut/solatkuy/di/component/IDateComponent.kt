package com.programmergabut.solatkuy.di.component

import com.programmergabut.solatkuy.data.model.prayerJson.Date
import com.programmergabut.solatkuy.di.module.DateModule
import dagger.Component

/*
 * Created by Katili Jiwo Adi Wiyono on 16/04/20.
 */

@Component(modules = [DateModule::class])
interface IDateComponent {
    fun getDate(): Date
}