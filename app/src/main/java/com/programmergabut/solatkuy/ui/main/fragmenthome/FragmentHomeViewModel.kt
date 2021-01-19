package com.programmergabut.solatkuy.ui.main.fragmenthome

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

const val TODAY_PRAYER_DATA_IS_NOT_FOUND = "Today prayer data is not found"
const val APPLICATION_OFFLINE = "Application Offline"
class FragmentMainViewModel @ViewModelInject constructor(
    private val prayerRepository: PrayerRepository,
    private val quranRepository: QuranRepository
): ViewModel() {

    val msApi1 = prayerRepository.observeMsApi1()

    private var isFavAyahCalled = MutableLiveData(false)
    val favAyah: LiveData<List<MsFavAyah>> = Transformations.switchMap(isFavAyahCalled) { isFirstLoad ->
        if (!isFirstLoad) {
            AbsentLiveData.create()
        } else {
            quranRepository.getListFavAyah()
        }
    }
    fun getMsFavAyah(value: Boolean = true) {
        this.isFavAyahCalled.value = value
    }

    private var _notifiedPrayer = MutableLiveData<Resource<List<NotifiedPrayer>>>()
    val notifiedPrayer: LiveData<Resource<List<NotifiedPrayer>>>
        get() = _notifiedPrayer
    fun syncNotifiedPrayer(msApi1: MsApi1) = viewModelScope.launch {
        _notifiedPrayer.postValue(Resource.loading(null))
        try {
            /* if you want to change the prayer time manually for testing,
             * uncomment it and comment the code below
             *
                prayerRepository.syncNotifiedPrayerTesting().let {
                _notifiedPrayer.postValue(Resource.success(it))
            } */

            val response = prayerRepository.fetchPrayerApi(msApi1).await()
            val result = prayerRepository.getListNotifiedPrayer()
            if(response.statusResponse == "1" && response.data.isNotEmpty()){
                val timings = getTodayTimings(response.data)
                if(timings != null){
                    val prayers = createPrayerTime(timings)
                    prayers.forEach { prayer -> prayerRepository.updatePrayerTime(prayer.key, prayer.value) }
                    _notifiedPrayer.postValue(Resource.success(result))
                }
                else{
                    _notifiedPrayer.postValue(Resource.error(result, TODAY_PRAYER_DATA_IS_NOT_FOUND))
                    return@launch
                }
            }
            else{
                _notifiedPrayer.postValue(Resource.error(result, APPLICATION_OFFLINE))
                return@launch
            }
        }
        catch (ex: Exception){
            _notifiedPrayer.postValue(Resource.error(null, ex.message.toString()))
        }
    }

    private fun getTodayTimings(data: List<Data>): Timings? {
        val currentDate = SimpleDateFormat("dd", Locale.getDefault()).format(Date())
        return data.find { obj -> obj.date.gregorian?.day == currentDate.toString() }?.timings
    }

    private fun createPrayerTime(timings: Timings): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        map[EnumConfig.FAJR] = timings.fajr
        map[EnumConfig.DHUHR] = timings.dhuhr
        map[EnumConfig.ASR] = timings.asr
        map[EnumConfig.MAGHRIB] = timings.maghrib
        map[EnumConfig.ISHA] = timings.isha
        map[EnumConfig.SUNRISE] = timings.sunrise
        return map
    }

    private var _readSurahEn = MutableLiveData<Resource<ReadSurahEnResponse>>()
    val readSurahEn: LiveData<Resource<ReadSurahEnResponse>>
        get() = _readSurahEn
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
    val prayer: LiveData<Resource<PrayerResponse>>
        get() = _prayer
    fun fetchPrayerApi(msApi1: MsApi1){
        viewModelScope.launch {
            _prayer.postValue(Resource.loading(null))
            try{
                val response  = prayerRepository.fetchPrayerApi(msApi1).await()
                if(response.statusResponse == "1"){
                    _prayer.postValue(Resource.success(response))
                }
                else{
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