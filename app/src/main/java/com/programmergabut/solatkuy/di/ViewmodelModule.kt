package com.programmergabut.solatkuy.di

import com.programmergabut.solatkuy.ui.boarding.BoardingViewModel
import com.programmergabut.solatkuy.ui.main.home.HomeViewModel
import com.programmergabut.solatkuy.ui.main.qibla.CompassViewModel
import com.programmergabut.solatkuy.ui.main.quran.listsurah.ListSurahViewModel
import com.programmergabut.solatkuy.ui.main.quran.readsurah.ReadSurahViewModel
import com.programmergabut.solatkuy.ui.main.setting.SettingViewModel
import com.programmergabut.solatkuy.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { CompassViewModel(get(),get()) }
    viewModel { SettingViewModel(get(),get()) }
    viewModel { SplashViewModel(get(), get()) }
    viewModel { ReadSurahViewModel(get(),get()) }
    viewModel { BoardingViewModel(get()) }
    viewModel { ListSurahViewModel(get(),get()) }
}




