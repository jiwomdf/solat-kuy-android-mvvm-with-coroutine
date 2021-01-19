package com.programmergabut.solatkuy.ui.main.fragmentcompass

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class FragmentCompassViewModel @ViewModelInject constructor(val prayerRepository: PrayerRepository): ViewModel() {

    val msApi1 = prayerRepository.observeMsApi1()

    private var _compass = MutableLiveData<Resource<CompassResponse>>()
    val compass: LiveData<Resource<CompassResponse>>
        get() = _compass
    fun fetchCompassApi(msApi1: MsApi1){
        viewModelScope.launch {
            _compass.postValue(Resource.loading(null))
            try {
                val response = prayerRepository.fetchCompass(msApi1).await()
                if(response.statusResponse == "1"){
                    _compass.postValue(Resource.success(response))
                } else{
                    _compass.postValue(Resource.error(null, response.messageResponse))
                }
            }
            catch (ex: Exception){
                _compass.postValue(Resource.error(null, ex.message.toString()))
            }
        }
    }

}