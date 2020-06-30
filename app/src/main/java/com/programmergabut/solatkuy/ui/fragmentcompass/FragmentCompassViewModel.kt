package com.programmergabut.solatkuy.ui.fragmentcompass

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.NetworkHelper
import kotlinx.coroutines.launch

class FragmentCompassViewModel @ViewModelInject constructor(val repository: Repository, val networkHelper: NetworkHelper): ViewModel() {

    val msApi1 = repository.getMsApi1()

    private var _compass = MutableLiveData<Resource<CompassResponse>>()
    val compass: LiveData<Resource<CompassResponse>>
        get() = _compass

    fun fetchCompassApi(msApi1: MsApi1){

        viewModelScope.launch {
            _compass.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                repository.fetchCompass(msApi1).let {
                    if (it.isSuccessful)
                        _compass.postValue(Resource.success(it.body()))
                    else
                        _compass.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
            else
                _compass.postValue(Resource.error("No internet connection", null))
        }

    }

}