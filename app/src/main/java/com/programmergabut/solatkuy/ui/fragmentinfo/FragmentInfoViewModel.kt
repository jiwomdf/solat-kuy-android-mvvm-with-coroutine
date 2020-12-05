package com.programmergabut.solatkuy.ui.fragmentinfo

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.util.EnumStatus
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

    var prayerStatus = MutableLiveData<Resource<Unit>>()
    private var _prayer = PrayerResponse(-1, listOf(), "")
    val prayer: PrayerResponse
        get() = _prayer

    fun fetchPrayerApi(msApi1: MsApi1){
        viewModelScope.launch {
            prayerStatus.postValue(Resource.loading(null))
            try{
                runIdlingResourceIncrement()
                prayerRepository.fetchPrayerApi(msApi1).let {
                    _prayer = it
                    prayerStatus.postValue(Resource.success(null))
                    runIdlingResourceDecrement()
                }
            }
            catch (ex: Exception){
                runIdlingResourceDecrement()
                prayerStatus.postValue(Resource.error(ex.message.toString(), null))
            }
        }
    }

}