package com.programmergabut.solatkuy.ui.fragmentinfo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.model.asmaalhusnaJson.AsmaAlHusnaApi
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.data.model.prayerJson.PrayerApi
import com.programmergabut.solatkuy.data.repository.Repository
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

/*
 * Created by Katili Jiwo Adi Wiyono on 25/04/20.
 */

class FragmentInfoViewModel(application: Application,  private val repository: Repository): AndroidViewModel(application) {

    var prayerApi = MutableLiveData<Resource<PrayerApi>>()
    var asmaAlHusnaApi = MutableLiveData<Resource<AsmaAlHusnaApi>>()
    val msApi1Local: LiveData<MsApi1> = repository.mMsApi1

    //Room
    init {
        prayerApi.postValue(Resource.loading(null))
        asmaAlHusnaApi.postValue(Resource.loading(null))
    }

    //Live Data
    fun fetchPrayerApi(latitude: String, longitude: String, method: String, month: String, year: String){
        prayerApi = repository.fetchPrayerApi(latitude, longitude, method, month, year)
    }

    fun fetchAsmaAlHusnaApi() {
        asmaAlHusnaApi = repository.getAsmaAlHusna()
    }
}