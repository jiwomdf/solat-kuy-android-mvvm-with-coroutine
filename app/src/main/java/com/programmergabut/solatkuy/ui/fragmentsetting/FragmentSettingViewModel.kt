package com.programmergabut.solatkuy.ui.fragmentsetting

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentSettingViewModel @ViewModelInject constructor(val prayerRepository: PrayerRepository): ViewModel() {

    companion object {
        const val SUCCESS_CHANGE_COORDINATE = "Success change the coordinate"
    }

    val msApi1 = prayerRepository.getMsApi1()

    var errMessage = MutableLiveData<String>()

    fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {

        if(msApi1.latitude.isEmpty() || msApi1.longitude.isEmpty() || msApi1.latitude == "." || msApi1.longitude == "."){
            errMessage.postValue("latitude and longitude cannot be empty")
            return@launch
        }

        val arrLatitude = msApi1.latitude.toCharArray()
        val arrLongitude = msApi1.longitude.toCharArray()

        if(arrLatitude[arrLatitude.size - 1] == '.' || arrLongitude[arrLongitude.size - 1] == '.'){
            errMessage.postValue("latitude and longitude cannot be ended by .")
            return@launch
        }

        if(arrLatitude[0] == '.' || arrLongitude[0] == '.'){
            errMessage.postValue("latitude and longitude cannot be started by .")
            return@launch
        }

        prayerRepository.updateMsApi1(msApi1)
        errMessage.postValue(SUCCESS_CHANGE_COORDINATE)
    }

}