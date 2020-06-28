package com.programmergabut.solatkuy.ui.fragmentsetting

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentSettingViewModel constructor(val repository: Repository): ViewModel() {

    private var _msApi1 = MutableLiveData<Resource<MsApi1>>()
    val msApi1: LiveData<Resource<MsApi1>>
        get() = _msApi1

    fun getMsApi1() = viewModelScope.launch {
        _msApi1.postValue(Resource.loading(null))

        repository.getMsApi1().let {
            _msApi1.postValue(Resource.success(it))
        }

    }

    fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {
        repository.updateMsApi1(msApi1)
    }

}