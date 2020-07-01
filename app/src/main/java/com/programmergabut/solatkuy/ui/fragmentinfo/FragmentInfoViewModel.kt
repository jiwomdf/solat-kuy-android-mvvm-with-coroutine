package com.programmergabut.solatkuy.ui.fragmentinfo

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

/*
 * Created by Katili Jiwo Adi Wiyono on 25/04/20.
 */

class FragmentInfoViewModel @ViewModelInject constructor(val repository: Repository): ViewModel() {

    val msApi1 = repository.getMsApi1()

    private var _prayer = MutableLiveData<Resource<PrayerResponse>>()
    val prayer: LiveData<Resource<PrayerResponse>>
        get() = _prayer

    fun fetchPrayerApi(msApi1: MsApi1){
        viewModelScope.launch {

            _prayer.postValue(Resource.loading(null))

            try{
                repository.fetchPrayerApi(msApi1).let {
                    _prayer.postValue(Resource.success(it))
                }
            }
            catch (ex: Exception){
                _prayer.postValue(Resource.error(ex.message.toString(), null))
            }

        }
    }

}