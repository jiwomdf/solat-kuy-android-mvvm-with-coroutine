package com.programmergabut.solatkuy.ui.fragmentsetting.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.data.repository.Repository
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentSettingViewModel(application: Application, private val repository: Repository): AndroidViewModel(application) {

    val msApi1Local: LiveData<MsApi1>

    //Room
    init {
        msApi1Local = repository.mMsApi1
    }

    fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {
        repository.updateMsApi1(msApi1.api1ID, msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)
    }


}