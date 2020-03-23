package com.programmergabut.solatkuy.ui.fragmentmain.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.model.PrayerLocal
import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import com.programmergabut.solatkuy.data.repository.Repository
import com.programmergabut.solatkuy.room.SolatKuyRoom
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentMainViewModel(application: Application): AndroidViewModel(application) {

    private val notifiedPrayerDao = SolatKuyRoom.getDataBase(application, viewModelScope).notifiedPrayerDao()
    private var repository: Repository? = null

    val prayerApi = MutableLiveData<Resource<PrayerApi>>()
    val prayerLocal: LiveData<List<PrayerLocal>>
    //val msApi1: LiveData<List<MsApi1>>

    //Room
    init {
        repository = Repository(application,viewModelScope)

        prayerLocal = repository!!.prayerLocal
    }

    fun update(prayerLocal: PrayerLocal) = viewModelScope.launch {
        repository?.update(prayerLocal)
    }

    //Live Data
    fun fetch(latitude: String, longitude: String, method: Int, month: String, year: String){
        viewModelScope.launch(Dispatchers.Default){
            prayerApi.postValue(Resource.success(Repository(getApplication(),viewModelScope).fetchPrayerApi(latitude,longitude, method, month, year)))
        }
    }


}