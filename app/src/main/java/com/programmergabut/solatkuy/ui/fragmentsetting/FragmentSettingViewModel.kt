package com.programmergabut.solatkuy.ui.fragmentsetting

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentSettingViewModel @ViewModelInject constructor(val repository: Repository): ViewModel() {

    val msApi1 = repository.getMsApi1()

    fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {
        repository.updateMsApi1(msApi1)
    }

}