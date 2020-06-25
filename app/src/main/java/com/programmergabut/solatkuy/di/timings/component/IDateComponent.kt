package com.programmergabut.solatkuy.di.timings.component

import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Date
import com.programmergabut.solatkuy.di.timings.module.DateModule
import dagger.Component

/*
 * Created by Katili Jiwo Adi Wiyono on 16/04/20.
 */

@Component(modules = [DateModule::class])
interface IDateComponent {
    fun getDate(): Date
}