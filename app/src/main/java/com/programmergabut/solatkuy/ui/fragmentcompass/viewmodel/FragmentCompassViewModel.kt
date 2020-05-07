package com.programmergabut.solatkuy.ui.fragmentcompass.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.model.compassJson.CompassApi
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.data.model.prayerJson.PrayerApi
import com.programmergabut.solatkuy.data.repository.Repository
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class FragmentCompassViewModel(a: Application, private val repository: Repository): AndroidViewModel(a) {

    var compassApi = MutableLiveData<Resource<CompassApi>>()
    val msApi1Local: LiveData<MsApi1>

    init {
        compassApi.postValue(Resource.loading(null))
        msApi1Local = repository.mMsApi1
    }

    //Live Data
    fun fetchCompassApi(latitude: String, longitude: String){
        compassApi = repository.getCompass(latitude, longitude)
    }

}