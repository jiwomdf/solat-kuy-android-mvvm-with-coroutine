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

class FragmentCompassViewModel(a: Application): AndroidViewModel(a) {

    val compassApi = MutableLiveData<Resource<CompassApi>>()
    val msApi1Local: LiveData<MsApi1>

    private var repository: Repository? = null

    init {
        compassApi.postValue(Resource.loading(null))

        /* init repository */
        repository = Repository(a,viewModelScope)

        msApi1Local = repository?.mMsApi1!!
    }

    //Live Data
    fun fetchCompassApi(latitude: String, longitude: String){

        viewModelScope.launch(Dispatchers.IO){
            try {
                compassApi.postValue(Resource.success(
                    Repository(getApplication(), viewModelScope)
                    .fetchQiblaApi(latitude,longitude)))
            }
            catch (ex: Exception){
                compassApi.postValue(Resource.error(ex.message.toString(), null))
            }

        }

    }

}