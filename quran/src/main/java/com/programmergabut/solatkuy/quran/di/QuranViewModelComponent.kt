package com.programmergabut.solatkuy.quran.di

import android.content.Context
import androidx.lifecycle.ViewModel
import com.programmergabut.solatkuy.di.SubModuleDependencies
import com.programmergabut.solatkuy.quran.ui.listsurah.ListSurahFragment
import com.programmergabut.solatkuy.quran.ui.listsurah.ListSurahViewModel
import com.programmergabut.solatkuy.quran.ui.readsurah.ReadSurahFragment
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton


@Component(
    dependencies = [SubModuleDependencies::class],
    modules = [QuranViewModelModule::class]
)
@Singleton
interface QuranViewModelComponent {

    fun inject(fragment: ListSurahFragment)
    fun inject(fragment: ReadSurahFragment)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun dependencies(component: SubModuleDependencies): Builder
        fun build(): QuranViewModelComponent
    }

}