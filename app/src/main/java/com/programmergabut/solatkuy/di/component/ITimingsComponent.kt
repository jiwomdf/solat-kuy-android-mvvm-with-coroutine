package com.programmergabut.solatkuy.di.component

import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Timings
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

/*
 * Created by Katili Jiwo Adi Wiyono on 16/04/20.
 */

@Component
interface ITimingsComponent {

    fun getTimings(): Timings

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun asr(@Named("asr") asr: String): Builder
        @BindsInstance
        fun dhuhr(@Named("dhuhr") dhuhr: String): Builder
        @BindsInstance
        fun fajr(@Named("fajr") fajr: String): Builder
        @BindsInstance
        fun imsak(@Named("imsak") imsak: String): Builder
        @BindsInstance
        fun isha(@Named("isha") isha: String): Builder
        @BindsInstance
        fun maghrib(@Named("maghrib") maghrib: String): Builder
        @BindsInstance
        fun midnight(@Named("midnight") midnight: String): Builder
        @BindsInstance
        fun sunrise(@Named("sunrise") sunrise: String): Builder
        @BindsInstance
        fun sunset(@Named("sunset") sunset: String): Builder

        fun build(): ITimingsComponent
    }

}