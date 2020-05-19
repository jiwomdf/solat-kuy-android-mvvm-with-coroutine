package com.programmergabut.solatkuy.ui.fragmentinfo.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.AsmaAlHusnaApi
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerApi
import com.programmergabut.solatkuy.util.Resource

/*
 * Created by Katili Jiwo Adi Wiyono on 25/04/20.
 */

class FragmentInfoViewModel(application: Application,  private val repository: Repository): AndroidViewModel(application) {

    private var idMsApi1 = MutableLiveData<MsApi1>()
    val prayerApi : MutableLiveData<Resource<PrayerApi>> = Transformations.switchMap(idMsApi1){
        repository.fetchPrayerApi(it)
    } as MutableLiveData<Resource<PrayerApi>>

    var asmaAlHusnaApi : MutableLiveData<Resource<AsmaAlHusnaApi>> = Transformations.switchMap(idMsApi1){
        repository.fetchAsmaAlHusna()
    } as MutableLiveData<Resource<AsmaAlHusnaApi>>

    val msApi1Local: LiveData<MsApi1> = repository.mMsApi1

    //Room
    init {
        prayerApi.postValue(Resource.loading(null))
        asmaAlHusnaApi.postValue(Resource.loading(null))
    }

    fun fetchAsmaAlHusna(msApi1: MsApi1){
        this.idMsApi1.value = msApi1
    }

    fun fetchPrayerApi(msApi1: MsApi1){
        this.idMsApi1.value = msApi1
    }

}