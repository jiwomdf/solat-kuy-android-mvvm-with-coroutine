package com.programmergabut.solatkuy.ui.fragmentmain.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.data.model.entity.PrayerLocal
import com.programmergabut.solatkuy.data.model.prayerJson.PrayerApi
import com.programmergabut.solatkuy.data.repository.Repository
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentMainViewModel(application: Application, private val repository: Repository): AndroidViewModel(application) {

    private var idMsApi1 = MutableLiveData<MsApi1>()

    val listPrayerLocal: LiveData<List<PrayerLocal>> = repository.mListPrayerLocal
    val msApi1Local: LiveData<MsApi1> = repository.mMsApi1

    val prayerApi : MutableLiveData<Resource<PrayerApi>> = Transformations.switchMap(idMsApi1){
        repository.fetchPrayerApi(it)
    } as MutableLiveData<Resource<PrayerApi>>

    init {
        prayerApi.postValue(Resource.loading(null))
    }

    fun fetchPrayerApi(msApi1: MsApi1){
        this.idMsApi1.value = msApi1
    }

    fun updateNotifiedPrayer(prayerLocal: PrayerLocal) = viewModelScope.launch {
        repository.updateNotifiedPrayer(prayerLocal)
    }

    fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = viewModelScope.launch {
        repository.updatePrayerIsNotified(prayerName, isNotified)
    }

}