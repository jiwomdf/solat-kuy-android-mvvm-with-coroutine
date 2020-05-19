package com.programmergabut.solatkuy.di.component

import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Gregorian
import com.programmergabut.solatkuy.di.module.GregorianModule
import dagger.Component

/*
 * Created by Katili Jiwo Adi Wiyono on 16/04/20.
 */

@Component(modules = [GregorianModule::class])
interface IGregorianComponent {
    fun getGregorian(): Gregorian
}