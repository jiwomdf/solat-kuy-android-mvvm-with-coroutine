package com.programmergabut.solatkuy.quran.di

import android.content.Context
import com.programmergabut.solatkuy.di.SubModuleDependencies
import com.programmergabut.solatkuy.quran.ui.listsurah.ListSurahFragment
import com.programmergabut.solatkuy.quran.ui.readsurah.ReadSurahFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Component(
    dependencies = [SubModuleDependencies::class],
    modules = [QuranViewModelModule::class]
)
@Singleton
interface QuranComponent {

    fun inject(fragment: ListSurahFragment)
    fun inject(fragment: ReadSurahFragment)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun dependencies(component: SubModuleDependencies): Builder
        fun build(): QuranComponent
    }

}