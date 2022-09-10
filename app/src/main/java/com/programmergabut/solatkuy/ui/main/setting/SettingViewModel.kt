package com.programmergabut.solatkuy.ui.main.setting

import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.repository.PrayerRepository
import com.programmergabut.solatkuy.data.local.localentity.MsConfiguration
import com.programmergabut.solatkuy.data.local.localentity.MsCalculationMethods
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.livedata.AbsentLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

@HiltViewModel
class SettingViewModel @Inject constructor(private val prayerRepository: PrayerRepository): ViewModel() {

    val msConfiguration = prayerRepository.observeMsConfiguration()

    private var _methods = MutableLiveData(false)
    val methods: LiveData<Resource<List<MsCalculationMethods>>> = Transformations.switchMap(_methods) { isFirstLoad ->
        if (!isFirstLoad) {
            AbsentLiveData.create()
        } else {
            prayerRepository.getMethods()
        }
    }
    fun getMethods(value: Boolean = true) {
        this._methods.value = value
    }

    fun updateMsConfiguration(msConfiguration: MsConfiguration) = viewModelScope.launch {
        prayerRepository.updateMsConfiguration(msConfiguration)
    }

    fun updateMsConfigurationMethod(api1ID: Int, methodID: String) = viewModelScope.launch {
        prayerRepository.updateMsConfigurationMethod(api1ID, methodID)
    }


}