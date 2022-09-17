package com.programmergabut.solatkuy.quran.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programmergabut.solatkuy.ViewModelFactory
import com.programmergabut.solatkuy.quran.ui.listsurah.ListSurahViewModel
import com.programmergabut.solatkuy.quran.ui.readsurah.ReadSurahViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.multibindings.IntoMap


@Module
@InstallIn(ViewModelComponent::class)
abstract class QuranViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @QuranViewModelKey(ListSurahViewModel::class)
    abstract fun bindListSurahViewModel(viewModel: ListSurahViewModel): ViewModel

    @Binds
    @IntoMap
    @QuranViewModelKey(ReadSurahViewModel::class)
    abstract fun bindReadSurahViewModel(viewModel: ReadSurahViewModel): ViewModel

}