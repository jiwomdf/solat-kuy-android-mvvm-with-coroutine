package com.programmergabut.solatkuy.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.ui.fragmentsetting.FragmentSettingViewModel
import kotlinx.coroutines.launch

class MainActivityViewModel @ViewModelInject constructor(val prayerRepository: PrayerRepository) : ViewModel(){

    companion object {
        const val SUCCESS_CHANGE_COORDINATE = "Success change the coordinate"
    }

    /* private var _msSetting = MutableLiveData<Resource<MsSetting>>()
    val msSetting: LiveData<Resource<MsSetting>>
        get() = _msSetting */

    val msSetting = prayerRepository.getMsSetting()
    /* viewModelScope.launch {

        _msSetting.postValue(Resource.loading(null))

        repository.getMsSetting().let {
            _msSetting.postValue(Resource.success(it.value))
        }
    } */

    /* fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {
        repository?.updateMsApi1(msApi1, this)
    } */

    /* fun updateSetting(msSetting: MsSetting) = viewModelScope.launch {
        repository?.updateMsSetting(msSetting.isHasOpenApp, this)
    } */

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
        errMessage.postValue(FragmentSettingViewModel.SUCCESS_CHANGE_COORDINATE)
    }

    fun updateIsHasOpenApp(isHasOpen: Boolean) = viewModelScope.launch{
        prayerRepository.updateIsHasOpenApp(isHasOpen)
    }

}