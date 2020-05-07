package com.programmergabut.solatkuy.ui.fragmentmain.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.data.model.entity.PrayerLocal
import com.programmergabut.solatkuy.data.model.prayerJson.PrayerApi
import com.programmergabut.solatkuy.data.repository.Repository
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentMainViewModel(application: Application, private val repository: Repository): AndroidViewModel(application) {

    var prayerApi = MutableLiveData<Resource<PrayerApi>>()
    val listPrayerLocal: LiveData<List<PrayerLocal>> = repository.mListPrayerLocal
    val msApi1Local: LiveData<MsApi1> = repository.mMsApi1

    init {
        prayerApi.postValue(Resource.loading(null))
    }

    fun updateNotifiedPrayer(prayerLocal: PrayerLocal) = viewModelScope.launch {
        repository.updateNotifiedPrayer(prayerLocal)
    }

    fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = viewModelScope.launch {
        repository.updatePrayerIsNotified(prayerName, isNotified)
    }

    //Live Data
    fun fetchPrayerApi(latitude: String, longitude: String, method: String, month: String, year: String){
        prayerApi = repository.fetchPrayerApi(latitude, longitude, method, month, year)
    }


}