package com.programmergabut.solatkuy.ui.splash

import androidx.lifecycle.ViewModel
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.local.SolatKuyRoom


class SplashViewModel(val prayerRepository: PrayerRepository, val solatKuyRoom: SolatKuyRoom) :
    ViewModel() {
    val msSetting = prayerRepository.observeMsSetting()
}
