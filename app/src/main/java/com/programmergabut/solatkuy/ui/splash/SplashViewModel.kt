package com.programmergabut.solatkuy.ui.splash

import androidx.lifecycle.ViewModel
import com.programmergabut.solatkuy.data.repository.PrayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(val prayerRepository: PrayerRepository): ViewModel() {
    val msSetting =  prayerRepository.observeMsSetting()
}