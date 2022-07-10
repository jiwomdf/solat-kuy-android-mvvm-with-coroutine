package com.programmergabut.solatkuy.quran.di

import androidx.lifecycle.ViewModel
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
    @IntoMap
    @QuranViewModelKey(ListSurahViewModel::class)
    abstract fun bindListSurahViewModel(viewModel: ListSurahViewModel): ViewModel

    @Binds
    @IntoMap
    @QuranViewModelKey(ReadSurahViewModel::class)
    abstract fun bindReadSurahViewModel(viewModel: ReadSurahViewModel): ViewModel

}