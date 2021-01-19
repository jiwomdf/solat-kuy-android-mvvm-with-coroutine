package com.programmergabut.solatkuy.ui.main.fragmentsetting

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentSettingViewModel @ViewModelInject constructor(private val prayerRepositoryImpl: PrayerRepository): ViewModel() {

    val msApi1 = prayerRepositoryImpl.observeMsApi1()

    var updateMessage = MutableLiveData<Resource<Unit>>()
    fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {

        if(msApi1.latitude.isEmpty() || msApi1.longitude.isEmpty() || msApi1.latitude == "." || msApi1.longitude == "."){
            updateMessage.postValue(Resource.error( null, "latitude and longitude cannot be empty"))
            return@launch
        }

        val arrLatitude = msApi1.latitude.toCharArray()
        val arrLongitude = msApi1.longitude.toCharArray()

        if(arrLatitude[arrLatitude.size - 1] == '.' || arrLongitude[arrLongitude.size - 1] == '.'){
            updateMessage.postValue(Resource.error( null, "latitude and longitude cannot be ended with ."))
            return@launch
        }

        if(arrLatitude[0] == '.' || arrLongitude[0] == '.'){
            updateMessage.postValue(Resource.error(null, "latitude and longitude cannot be started with ."))
            return@launch
        }

        prayerRepositoryImpl.updateMsApi1(msApi1)
        updateMessage.postValue(Resource.error(null, "Success change the coordinate"))
    }

}