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

class FragmentMainViewModel(application: Application): AndroidViewModel(application) {

    private var repository: Repository? = null

    val prayerApi = MutableLiveData<Resource<PrayerApi>>()
    val listPrayerLocal: LiveData<List<PrayerLocal>>
    val msApi1Local: LiveData<MsApi1>

    //Room
    init {
        repository = Repository(application,viewModelScope)

        listPrayerLocal = repository!!.mListPrayerLocal
        msApi1Local = repository!!.mMsApi1Local

        prayerApi.postValue(Resource.loading(null))
    }

    fun updateNotifiedPrayer(prayerLocal: PrayerLocal) = viewModelScope.launch {
        repository?.updateNotifiedPrayer(prayerLocal)
    }

    fun updateNotifiedPrayerWithoutTime(prayerLocal: PrayerLocal) = viewModelScope.launch {
        repository?.updatePrayerIsNotified(prayerLocal)
    }

    //Live Data
    fun fetchPrayerApi(latitude: String, longitude: String, method: String, month: String, year: String){
        viewModelScope.launch(Dispatchers.IO){
            try {
                prayerApi.postValue(Resource.success(Repository(getApplication(),viewModelScope)
                    .fetchPrayerApi(latitude,longitude, method, month, year)))
            }
            catch (ex: Exception){
                prayerApi.postValue(Resource.error(ex.message.toString(), null))
            }

        }


    }


}