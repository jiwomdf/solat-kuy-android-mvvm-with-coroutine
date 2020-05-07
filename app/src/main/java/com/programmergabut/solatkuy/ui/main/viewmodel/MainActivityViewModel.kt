package com.programmergabut.solatkuy.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.data.model.entity.MsSetting
import com.programmergabut.solatkuy.data.repository.Repository
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application, repository: Repository) : AndroidViewModel(application){

    val msSetting: LiveData<MsSetting>
    private var repository: Repository? = null

    //Room
    init {
        msSetting = repository.mMsSetting
    }

    fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {
        repository?.updateMsApi1(msApi1.api1ID, msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)
    }

    fun updateSetting(msSetting: MsSetting) = viewModelScope.launch {
        repository?.updateMsSetting(msSetting.isHasOpenApp)
    }

}