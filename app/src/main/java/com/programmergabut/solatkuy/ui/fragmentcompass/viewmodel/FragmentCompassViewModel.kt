package com.programmergabut.solatkuy.ui.fragmentcompass.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.util.Resource

class FragmentCompassViewModel(a: Application, private val repository: Repository): AndroidViewModel(a) {

    private var coordinateID = MutableLiveData<MsApi1>()

    var compassResponse : MutableLiveData<Resource<CompassResponse>> =  Transformations.switchMap(coordinateID){
        repository.fetchCompass(it)
    } as MutableLiveData<Resource<CompassResponse>>

    val msApi1Local = repository.getMsApi1()

    fun fetchCompassApi(msApi1: MsApi1){
        this.coordinateID.value = msApi1
    }

}