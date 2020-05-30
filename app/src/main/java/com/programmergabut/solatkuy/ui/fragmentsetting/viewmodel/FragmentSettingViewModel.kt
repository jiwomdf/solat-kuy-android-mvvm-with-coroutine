package com.programmergabut.solatkuy.ui.fragmentsetting.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.Repository
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentSettingViewModel(application: Application, private val repository: Repository): AndroidViewModel(application) {

    val msApi1 = repository.getMsApi1()

    fun updateMsApi1(msApi1: MsApi1) = repository.updateMsApi1(msApi1)

}