package com.programmergabut.solatkuy.ui.main.home

import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.data.remote.json.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.livedata.AbsentLiveData
import kotlinx.coroutines.launch


class HomeViewModel(
    private val prayerRepository: PrayerRepository,
    private val quranRepository: QuranRepository
) : ViewModel() {

    val msApi1 = prayerRepository.observeMsApi1()
    private val _msApi1 = MutableLiveData<MsApi1>()
    val notifiedPrayer = Transformations.switchMap(_msApi1) {
        if (_msApi1 == null) {
            AbsentLiveData.create()
        } else {
            prayerRepository.getListNotifiedPrayer(it)
        }
    }

    fun getListNotifiedPrayer(msApi1: MsApi1) {
        _msApi1.value = msApi1
    }

    private var _readSurahEn = MutableLiveData<Resource<ReadSurahEnResponse>>()
    val readSurahEn: LiveData<Resource<ReadSurahEnResponse>> = _readSurahEn
    fun fetchReadSurahEn(nInSurah: Int) = viewModelScope.launch {
        _readSurahEn.postValue(Resource.loading(null))
        try {
            val response = quranRepository.fetchReadSurahEn(nInSurah).await()
            if (response.responseStatus == "1") {
                _readSurahEn.postValue(Resource.success(response))
            } else {
                _readSurahEn.postValue(Resource.error(null, response.message))
            }
        } catch (e: Exception) {
            _readSurahEn.postValue(Resource.error(null, e.message.toString()))
        }
    }

    private var _prayer = MutableLiveData<Resource<PrayerResponse>>()
    val prayer: LiveData<Resource<PrayerResponse>> = _prayer
    fun fetchPrayerApi(msApi1: MsApi1) = viewModelScope.launch {
        _prayer.postValue(Resource.loading(null))
        try {
            val response = prayerRepository.fetchPrayerApi(msApi1).await()
            if (response.responseStatus == "1") {
                _prayer.postValue(Resource.success(response))
            } else {
                _prayer.postValue(Resource.error(null, response.message))
            }
        } catch (ex: Exception) {
            _prayer.postValue(Resource.error(null, ex.message.toString()))
        }
    }

    private var isSettingCalled = MutableLiveData(false)
    val msSetting: LiveData<MsSetting> = Transformations.switchMap(isSettingCalled) { isFirstLoad ->
        if (!isFirstLoad) {
            AbsentLiveData.create()
        } else {
            prayerRepository.observeMsSetting()
        }
    }

    fun getMsSetting(value: Boolean = true) {
        this.isSettingCalled.value = value
    }

    fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {
        prayerRepository.updateMsApi1(msApi1)
    }

    fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = viewModelScope.launch {
        prayerRepository.updatePrayerIsNotified(prayerName, isNotified)
    }

    fun updateMsApi1MonthAndYear(api1ID: Int, month: String, year: String) = viewModelScope.launch {
        prayerRepository.updateMsApi1MonthAndYear(api1ID, month, year)
    }
}