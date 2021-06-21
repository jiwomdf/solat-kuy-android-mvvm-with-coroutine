package com.programmergabut.solatkuy.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.programmergabut.solatkuy.data.PrayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(val prayerRepository: PrayerRepository): ViewModel() {
    val msSetting =  prayerRepository.observeMsSetting()
}