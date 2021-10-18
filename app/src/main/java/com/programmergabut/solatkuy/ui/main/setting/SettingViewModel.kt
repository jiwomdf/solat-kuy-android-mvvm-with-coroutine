package com.programmergabut.solatkuy.ui.main.setting

import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsCalculationMethods
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.SharedPrefUtil
import com.programmergabut.solatkuy.util.livedata.AbsentLiveData
import kotlinx.coroutines.launch


class SettingViewModel(
    private val prayerRepository: PrayerRepository,
    val sharedPrefUtil: SharedPrefUtil
) :
    ViewModel() {

    val msApi1 = prayerRepository.observeMsApi1()

    private var _methods = MutableLiveData(false)
    val methods: LiveData<Resource<List<MsCalculationMethods>>> =
        Transformations.switchMap(_methods) { isFirstLoad ->
            if (!isFirstLoad) {
                AbsentLiveData.create()
            } else {
                prayerRepository.getMethods()
            }
        }

    fun getMethods(value: Boolean = true) {
        this._methods.value = value
    }

    fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {
        prayerRepository.updateMsApi1(msApi1)
    }

    fun updateMsApi1Method(api1ID: Int, methodID: String) = viewModelScope.launch {
        prayerRepository.updateMsApi1Method(api1ID, methodID)
    }


}