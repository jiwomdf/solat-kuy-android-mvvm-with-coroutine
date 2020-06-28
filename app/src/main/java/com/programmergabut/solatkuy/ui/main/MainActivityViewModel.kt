package com.programmergabut.solatkuy.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch

class MainActivityViewModel constructor(private val repository: Repository) : ViewModel(){

    private var _msSetting = MutableLiveData<Resource<MsSetting>>()
    val msSetting: LiveData<Resource<MsSetting>>
        get() = _msSetting

    fun getMsSetting() = viewModelScope.launch {

        _msSetting.postValue(Resource.loading(null))

        repository.getMsSetting().let {
            _msSetting.postValue(Resource.success(it))
        }
    }

    /* fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {
        repository?.updateMsApi1(msApi1, this)
    } */

    /* fun updateSetting(msSetting: MsSetting) = viewModelScope.launch {
        repository?.updateMsSetting(msSetting.isHasOpenApp, this)
    } */

}