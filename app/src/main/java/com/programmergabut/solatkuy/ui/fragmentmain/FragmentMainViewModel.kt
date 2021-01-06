package com.programmergabut.solatkuy.ui.fragmentmain

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Timings
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.EnumConfig
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.idlingresource.RunIdlingResourceHelper.Companion.runIdlingResourceDecrement
import com.programmergabut.solatkuy.util.idlingresource.RunIdlingResourceHelper.Companion.runIdlingResourceIncrement
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

    val msApi1 = prayerRepository.observeMsApi1()

    private var _favAyah = MutableLiveData<List<MsFavAyah>>()
    val favAyah: LiveData<List<MsFavAyah>>
        get() = _favAyah
    fun getMsFavAyah() {
        viewModelScope.launch {
            val result = quranRepository.getListFavAyah()
            _favAyah.postValue(result)
        }
    }

    private var _notifiedPrayer = MutableLiveData<Resource<List<NotifiedPrayer>>>()
    val notifiedPrayer: LiveData<Resource<List<NotifiedPrayer>>>
        get() = _notifiedPrayer
    fun syncNotifiedPrayer(msApi1: MsApi1) = viewModelScope.launch {
        _notifiedPrayer.postValue(Resource.loading(null))
        try {
            runIdlingResourceIncrement()

            /* if you want to change the prayer time manually for testing,
             * uncomment it and comment the code below
             *
                prayerRepository.syncNotifiedPrayerTesting().let {
                _notifiedPrayer.postValue(Resource.success(it))
                runIdlingResourceDecrement()
            } */

            val response = prayerRepository.fetchPrayerApi(msApi1).await()
            val result = prayerRepository.getListNotifiedPrayer()

            if(response.statusResponse == "1"){
                val currentDate = SimpleDateFormat("dd", Locale.getDefault()).format(Date())
                val timings = response.data.find { obj -> obj.date.gregorian?.day == currentDate.toString() }?.timings

                if(timings != null){
                    val prayers = createPrayerTime(timings)
                    prayers.forEach { prayer -> prayerRepository.updatePrayerTime(prayer.key, prayer.value) }
                    _notifiedPrayer.postValue(Resource.success(result))
                    runIdlingResourceDecrement()
                }
                else{
                    _notifiedPrayer.postValue(Resource.error("Today prayer data is not found", result))
                    runIdlingResourceDecrement()
                    return@launch
                }
            }
            else{
                _notifiedPrayer.postValue(Resource.error("Application Offline", result))
                runIdlingResourceDecrement()
                return@launch
            }
        }
        catch (ex: Exception){
            _notifiedPrayer.postValue(Resource.error(ex.message.toString(), null))
            runIdlingResourceDecrement()
        }
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
            runIdlingResourceIncrement()
            val response = quranRepository.fetchReadSurahEn(nInSurah).await()
            if(response.statusResponse == "1"){
                _readSurahEn.postValue(Resource.success(response))
            }
            else{
                _readSurahEn.postValue(Resource.error(response.messageResponse, null))
            }
            runIdlingResourceDecrement()
        }
        catch (e: Exception){
            _readSurahEn.postValue(Resource.error(e.message.toString(), null))
            runIdlingResourceDecrement()
        }

    }

    private var _prayer = MutableLiveData<Resource<PrayerResponse>>()
    val prayer: LiveData<Resource<PrayerResponse>>
        get() = _prayer
    fun fetchPrayerApi(msApi1: MsApi1){
        viewModelScope.launch {

            _prayer.postValue(Resource.loading(null))
            try{
                runIdlingResourceIncrement()
                val response  = prayerRepository.fetchPrayerApi(msApi1).await()
                if(response.statusResponse == "1"){
                    _prayer.postValue(Resource.success(response))
                    runIdlingResourceDecrement()
                }
                else{
                    _prayer.postValue(Resource.error(response.messageResponse, null))
                    runIdlingResourceDecrement()
                }
            }
            catch (ex: Exception){
                _prayer.postValue(Resource.error(ex.message.toString(), null))
                runIdlingResourceDecrement()
            }

        }
    }

    private var _setting = MutableLiveData<MsSetting>()
    val msSetting: LiveData<MsSetting>
        get() = _setting
    fun getMsSetting(){
        viewModelScope.launch {
            val msSetting = prayerRepository.getMsSetting()
            _setting.postValue(msSetting)
        }
    }

    /*fun updateNotifiedPrayer(NotifiedPrayer: NotifiedPrayer){
        repository.updateNotifiedPrayer(NotifiedPrayer)
    }*/

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