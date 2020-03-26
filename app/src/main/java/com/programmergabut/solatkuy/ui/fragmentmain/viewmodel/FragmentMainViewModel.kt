package com.programmergabut.solatkuy.ui.fragmentmain.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.model.MsApi1
import com.programmergabut.solatkuy.data.model.PrayerLocal
import com.programmergabut.solatkuy.data.model.prayerJson.PrayerApi
import com.programmergabut.solatkuy.data.repository.Repository
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentMainViewModel(application: Application): AndroidViewModel(application) {

    private var repository: Repository? = null

    val prayerApi = MutableLiveData<Resource<PrayerApi>>()
    val prayerLocal: LiveData<List<PrayerLocal>>
    val msApi1Local: LiveData<MsApi1>

    //Room
    init {
        repository = Repository(application,viewModelScope)

        prayerLocal = repository!!.mPrayerLocal
        msApi1Local = repository!!.mMsApi1Local
    }

    fun update(prayerLocal: PrayerLocal) = viewModelScope.launch {
        repository?.updateNotifiedPrayer(prayerLocal)
    }

    //Live Data
    fun fetchPrayerApi(latitude: String, longitude: String, method: String, month: String, year: String){
        viewModelScope.launch(Dispatchers.Default){
            prayerApi.postValue(Resource.success(Repository(getApplication(),viewModelScope).fetchPrayerApi(latitude,longitude, method, month, year)))
        }
    }


}