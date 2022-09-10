package com.programmergabut.solatkuy.ui.main.qibla

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.repository.PrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsConfiguration
import com.programmergabut.solatkuy.data.remote.json.compassJson.CompassResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompassViewModel @Inject constructor(val prayerRepository: PrayerRepository): ViewModel() {

    val msConfiguration = prayerRepository.observeMsConfiguration()

    private var _compass = MutableLiveData<Resource<CompassResponse>>()
    val compass: LiveData<Resource<CompassResponse>> = _compass
    fun fetchCompassApi(msConfiguration: MsConfiguration){
        viewModelScope.launch {
            _compass.postValue(Resource.loading(null))
            try {
                val response = prayerRepository.fetchQibla(msConfiguration).await()
                if(response.status == Status.Success){
                    _compass.postValue(Resource.success(response.data))
                } else {
                    _compass.postValue(Resource.error(null, response.message))
                }
            }
            catch (ex: Exception){
                _compass.postValue(Resource.error(null, ex.message.toString()))
            }
        }
    }

}