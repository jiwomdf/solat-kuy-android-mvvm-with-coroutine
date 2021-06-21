package com.programmergabut.solatkuy.ui.main.setting

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

@HiltViewModel
class FragmentSettingViewModel @Inject constructor(private val prayerRepositoryImpl: PrayerRepository): ViewModel() {

    val msApi1 = prayerRepositoryImpl.observeMsApi1()

    fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {
        prayerRepositoryImpl.updateMsApi1(msApi1)
    }

}