package com.programmergabut.solatkuy.ui.fragmentcompass

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.NetworkHelper
import kotlinx.coroutines.launch

class FragmentCompassViewModel
@ViewModelInject constructor(val repository: Repository, val networkHelper: NetworkHelper): ViewModel() {

    private var _msApi1 = MutableLiveData<Resource<MsApi1>>()
    val msApi1: LiveData<Resource<MsApi1>>
        get() = _msApi1

    fun getMsApi1() = viewModelScope.launch {

        _msApi1.postValue(Resource.loading(null))

        repository.getMsApi1().let {
            _msApi1.postValue(Resource.success(it))
        }

    }

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