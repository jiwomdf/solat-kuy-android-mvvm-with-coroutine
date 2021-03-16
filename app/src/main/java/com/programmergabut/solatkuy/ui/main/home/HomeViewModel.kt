package com.programmergabut.solatkuy.ui.main.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Data
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Timings
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.livedata.AbsentLiveData
import com.programmergabut.solatkuy.util.EnumConfig
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentMainViewModel @ViewModelInject constructor(
    private val prayerRepository: PrayerRepository,
    private val quranRepository: QuranRepository
): ViewModel() {

    private val TODAY_PRAYER_DATA_IS_NOT_FOUND = "Today prayer data is not found"
    private val APPLICATION_OFFLINE = "Application Offline"
    private val APPLICATION_ONLINE = "Application Online"

    val msApi1 = prayerRepository.observeMsApi1()

    private var isFavAyahCalled = MutableLiveData(false)
    val favAyah: LiveData<List<MsFavAyah>> = Transformations.switchMap(isFavAyahCalled) { isFirstLoad ->
        if (!isFirstLoad) {
            AbsentLiveData.create()
        } else {
            quranRepository.observeListFavAyah()
        }
    }
    fun getMsFavAyah(value: Boolean = true) {
        this.isFavAyahCalled.value = value
    }

    private var _notifiedPrayer = MutableLiveData<Resource<List<NotifiedPrayer>>>()
    val notifiedPrayer: LiveData<Resource<List<NotifiedPrayer>>> = _notifiedPrayer
    fun getListNotifiedPrayer(msApi1: MsApi1) = viewModelScope.launch {
        _notifiedPrayer.postValue(Resource.loading(null))
        try {
            val response = prayerRepository.fetchPrayerApi(msApi1).await()
            val result = prayerRepository.getListNotifiedPrayer()
            if(response.statusResponse == "1" && response.data.isNotEmpty() && !result.isNullOrEmpty()){
                val timings = getTodayTimings(response.data)
                if(timings != null) {
                    val updatedResult = saveRemotePrayerData(timings)
                    if(!updatedResult.isNullOrEmpty()){
                        _notifiedPrayer.postValue(Resource.success(updatedResult, APPLICATION_ONLINE))
                    } else {
                        _notifiedPrayer.postValue(Resource.success(result))
                    }
                } else {
                    _notifiedPrayer.postValue(Resource.error(result, TODAY_PRAYER_DATA_IS_NOT_FOUND))
                }
            } else {
                if(result.isNullOrEmpty()){
                    _notifiedPrayer.postValue(Resource.error(result))
                } else {
                    _notifiedPrayer.postValue(Resource.error(result, APPLICATION_OFFLINE))
                }
            }
        }
        catch (ex: Exception){
            _notifiedPrayer.postValue(Resource.error(null, ex.message.toString()))
        }
    }

    private suspend fun saveRemotePrayerData(timings: Timings): List<NotifiedPrayer>? {
        val prayerMap = mutableMapOf<String, String>()
        prayerMap[EnumConfig.FAJR] = timings.fajr
        prayerMap[EnumConfig.DHUHR] = timings.dhuhr
        prayerMap[EnumConfig.ASR] = timings.asr
        prayerMap[EnumConfig.MAGHRIB] = timings.maghrib
        prayerMap[EnumConfig.ISHA] = timings.isha
        prayerMap[EnumConfig.SUNRISE] = timings.sunrise
        prayerMap.forEach { prayer ->
            prayerRepository.updatePrayerTime(prayer.key, prayer.value)
        }
        return prayerRepository.getListNotifiedPrayer()
    }

    private fun getTodayTimings(data: List<Data>): Timings? {
        val currentDate = SimpleDateFormat("dd", Locale.getDefault()).format(Date())
        return data.find { obj -> obj.date.gregorian?.day == currentDate.toString() }?.timings
    }

    private var _readSurahEn = MutableLiveData<Resource<ReadSurahEnResponse>>()
    val readSurahEn: LiveData<Resource<ReadSurahEnResponse>> = _readSurahEn
    fun fetchReadSurahEn(nInSurah: Int) = viewModelScope.launch{
        _readSurahEn.postValue(Resource.loading(null))
        try {
            val response = quranRepository.fetchReadSurahEn(nInSurah).await()
            if(response.statusResponse == "1"){
                _readSurahEn.postValue(Resource.success(response))
            } else {
                _readSurahEn.postValue(Resource.error(null, response.messageResponse))
            }
        }
        catch (e: Exception){
            _readSurahEn.postValue(Resource.error(null, e.message.toString()))
        }
    }

    private var _prayer = MutableLiveData<Resource<PrayerResponse>>()
    val prayer: LiveData<Resource<PrayerResponse>> = _prayer
    fun fetchPrayerApi(msApi1: MsApi1){
        viewModelScope.launch {
            _prayer.postValue(Resource.loading(null))
            try{
                val response  = prayerRepository.fetchPrayerApi(msApi1).await()
                if(response.statusResponse == "1"){
                    _prayer.postValue(Resource.success(response))
                } else {
                    _prayer.postValue(Resource.error(null, response.messageResponse))
                }
            }
            catch (ex: Exception){
                _prayer.postValue(Resource.error(null, ex.message.toString()))
            }
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
    fun getMsSetting(value: Boolean = true){
        this.isSettingCalled.value = value
    }

    fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {
        prayerRepository.updateMsApi1(msApi1)
    }
    fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = viewModelScope.launch {
        prayerRepository.updatePrayerIsNotified(prayerName, isNotified)
    }
    fun updateIsUsingDBQuotes(isUsingDBQuotes: Boolean) = viewModelScope.launch {
        prayerRepository.updateIsUsingDBQuotes(isUsingDBQuotes)
    }
    fun updateMsApi1MonthAndYear(api1ID: Int, month: String, year:String) = viewModelScope.launch{
        prayerRepository.updateMsApi1MonthAndYear(api1ID, month, year)
    }
}