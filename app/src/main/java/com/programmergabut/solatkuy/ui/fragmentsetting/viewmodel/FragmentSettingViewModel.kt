package com.programmergabut.solatkuy.ui.fragmentsetting.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.model.dao.MsApi1
import com.programmergabut.solatkuy.data.repository.Repository
import kotlinx.coroutines.launch

class FragmentSettingViewModel(application: Application): AndroidViewModel(application) {

    val msApi1Local: LiveData<MsApi1>

    private var repository: Repository? = null

    //Room
    init {
        repository = Repository(application,viewModelScope)

        msApi1Local = repository!!.mMsApi1Local
    }

    fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {
        repository?.updateMsApi1(msApi1.api1ID, msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year)
    }


}