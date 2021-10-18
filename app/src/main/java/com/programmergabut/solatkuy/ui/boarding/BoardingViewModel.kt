package com.programmergabut.solatkuy.ui.boarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import kotlinx.coroutines.launch


class BoardingViewModel(val prayerRepository: PrayerRepository) : ViewModel() {

    val msSetting = prayerRepository.observeMsSetting()

    fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {
        prayerRepository.updateMsApi1(msApi1)
    }

    fun updateIsHasOpenApp(isHasOpen: Boolean) = viewModelScope.launch {
        prayerRepository.updateIsHasOpenApp(isHasOpen)
    }

}