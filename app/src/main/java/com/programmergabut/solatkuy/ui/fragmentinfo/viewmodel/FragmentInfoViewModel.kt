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

    private var msApi1Param = MutableLiveData<MsApi1>()
    val prayerApi : MutableLiveData<Resource<PrayerApi>> = Transformations.switchMap(msApi1Param){
        repository.fetchPrayerApi(it)
    } as MutableLiveData<Resource<PrayerApi>>

    val asmaAlHusnaApi : MutableLiveData<Resource<AsmaAlHusnaApi>> = Transformations.switchMap(msApi1Param){
        repository.fetchAsmaAlHusna()
    } as MutableLiveData<Resource<AsmaAlHusnaApi>>

    val msApi1Local: LiveData<MsApi1> = repository.getMsApi1()

    //Room
    init {
        prayerApi.postValue(Resource.loading(null))
        asmaAlHusnaApi.postValue(Resource.loading(null))
    }

    fun fetchAsmaAlHusna(msApi1: MsApi1){
        this.msApi1Param.value = msApi1
    }

    fun fetchPrayerApi(msApi1: MsApi1){
        this.msApi1Param.value = msApi1
    }

}