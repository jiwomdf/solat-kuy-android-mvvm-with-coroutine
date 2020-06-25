package com.programmergabut.solatkuy.di.timings.component

import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Gregorian
import com.programmergabut.solatkuy.di.timings.module.GregorianModule
import dagger.Component

/*
 * Created by Katili Jiwo Adi Wiyono on 16/04/20.
 */

@Component(modules = [GregorianModule::class])
interface IGregorianComponent {
    fun getGregorian(): Gregorian
}