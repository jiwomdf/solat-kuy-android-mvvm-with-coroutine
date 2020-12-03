package com.programmergabut.solatkuy.ui.fragmentinfo

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceDecrement
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceIncrement
import kotlinx.coroutines.launch
import java.lang.Exception

/*
 * Created by Katili Jiwo Adi Wiyono on 25/04/20.
 */

class FragmentInfoViewModel @ViewModelInject constructor(val prayerRepository: PrayerRepository): ViewModel() {

    val msApi1 = prayerRepository.getMsApi1()

    private var _prayer = MutableLiveData<Resource<PrayerResponse>>()
    val prayer: LiveData<Resource<PrayerResponse>>
        get() = _prayer

    fun fetchPrayerApi(msApi1: MsApi1){
        viewModelScope.launch {

            _prayer.postValue(Resource.loading(null))

            try{
                runIdlingResourceIncrement()
                prayerRepository.fetchPrayerApi(msApi1).let {
                    _prayer.postValue(Resource.success(it))
                    runIdlingResourceDecrement()
                }
            }
            catch (ex: Exception){
                runIdlingResourceDecrement()
                _prayer.postValue(Resource.error(ex.message.toString(), null))
            }

        }
    }

}