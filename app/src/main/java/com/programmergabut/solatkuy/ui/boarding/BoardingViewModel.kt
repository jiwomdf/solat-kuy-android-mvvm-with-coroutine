package com.programmergabut.solatkuy.ui.boarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.repository.PrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardingViewModel @Inject constructor(val prayerRepository: PrayerRepository) : ViewModel(){

    val msSetting =  prayerRepository.observeMsSetting()

    fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {
        prayerRepository.updateMsApi1(msApi1)
    }

    fun updateIsHasOpenApp(isHasOpen: Boolean) = viewModelScope.launch{
        prayerRepository.updateIsHasOpenApp(isHasOpen)
    }

}