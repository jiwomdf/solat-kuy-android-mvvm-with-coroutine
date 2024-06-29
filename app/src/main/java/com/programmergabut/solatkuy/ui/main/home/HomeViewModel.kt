package com.programmergabut.solatkuy.ui.main.home

import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.repository.PrayerRepository
import com.programmergabut.solatkuy.data.repository.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsConfiguration
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.data.remote.json.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.livedata.AbsentLiveData
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prayerRepository: PrayerRepository,
    private val quranRepository: QuranRepository
): ViewModel() {

    val msConfiguration = prayerRepository.observeMsConfiguration()
    private val _msConfiguration = MutableLiveData<MsConfiguration>()
    val notifiedPrayer = _msConfiguration.switchMap{
        prayerRepository.getListNotifiedPrayer(it)
    }
    fun getListNotifiedPrayer(msConfiguration: MsConfiguration){
        _msConfiguration.value = msConfiguration
    }

    private var _readSurahEn = MutableLiveData<Resource<ReadSurahEnResponse>>()
    val readSurahEn: LiveData<Resource<ReadSurahEnResponse>> = _readSurahEn
    fun fetchReadSurahEn(nInSurah: Int) = viewModelScope.launch{
        _readSurahEn.postValue(Resource.loading(null))
        try {
            val response = quranRepository.fetchReadSurahEn(nInSurah).await()
            if(response.status == Status.Success){
                _readSurahEn.postValue(Resource.success(response.data))
            } else {
                _readSurahEn.postValue(Resource.error(null, response.message))
            }
        }
        catch (e: Exception){
            _readSurahEn.postValue(Resource.error(null, e.message.toString()))
        }
    }

    private var _prayer = MutableLiveData<Resource<PrayerResponse>>()
    val prayer: LiveData<Resource<PrayerResponse>> = _prayer
    fun fetchPrayerApi(msConfiguration: MsConfiguration) = viewModelScope.launch {
        _prayer.postValue(Resource.loading(null))
        try{
            val response  = prayerRepository.fetchPrayerApi(msConfiguration).await()
            if(response.status == Status.Success){
                _prayer.postValue(Resource.success(response.data))
            } else {
                _prayer.postValue(Resource.error(null, response.message))
            }
        }
        catch (ex: Exception){
            _prayer.postValue(Resource.error(null, ex.message.toString()))
        }
    }

    private var isSettingCalled = MutableLiveData(false)
    val msSetting: LiveData<MsSetting> = isSettingCalled.switchMap { isFirstLoad ->
        if (!isFirstLoad) {
            AbsentLiveData.create()
        } else {
            prayerRepository.observeMsSetting()
        }
    }
    fun getMsSetting(value: Boolean = true){
        this.isSettingCalled.value = value
    }

    fun updateMsConfiguration(msConfiguration: MsConfiguration) = viewModelScope.launch {
        prayerRepository.updateMsConfiguration(msConfiguration)
    }
    fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = viewModelScope.launch {
        prayerRepository.updatePrayerIsNotified(prayerName, isNotified)
    }
    fun updateMsConfigurationMonthAndYear(api1ID: Int, month: String, year:String) = viewModelScope.launch{
        prayerRepository.updateMsConfigurationMonthAndYear(api1ID, month, year)
    }
}