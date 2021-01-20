package com.programmergabut.solatkuy.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.programmergabut.solatkuy.data.PrayerRepository

class SplashViewModel @ViewModelInject constructor(val prayerRepository: PrayerRepository): ViewModel() {
    val msSetting =  prayerRepository.observeMsSetting()
}