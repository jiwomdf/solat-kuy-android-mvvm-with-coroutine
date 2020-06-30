package com.programmergabut.solatkuy.ui.fragmentinfo

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.NetworkHelper
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/04/20.
 */

class FragmentInfoViewModel @ViewModelInject constructor(val repository: Repository, val networkHelper: NetworkHelper): ViewModel() {


    private var _prayer = MutableLiveData<Resource<PrayerResponse>>()
    val prayer: LiveData<Resource<PrayerResponse>>
        get() = _prayer

    val msApi1 = repository.getMsApi1()


    fun fetchPrayerApi(msApi1: MsApi1){
        viewModelScope.launch {

            _prayer.postValue(Resource.loading(null))

            if (networkHelper.isNetworkConnected()) {
                repository.fetchPrayerApi(msApi1).let {
                    if (it.isSuccessful)
                        _prayer.postValue(Resource.success(it.body()))
                    else
                        _prayer.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
            else
                _prayer.postValue(Resource.error("No internet connection", null))
        }
    }

}