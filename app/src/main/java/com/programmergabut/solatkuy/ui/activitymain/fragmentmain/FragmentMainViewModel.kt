package com.programmergabut.solatkuy.ui.activitymain.fragmentmain

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
import com.programmergabut.solatkuy.util.EnumConfig.Companion.IS_TESTING
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceDecrement
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceIncrement
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

    val msApi1 = prayerRepository.getMsApi1()

    private var _favAyah = MutableLiveData<Int>()
    val favAyah: LiveData<Resource<List<MsFavAyah>>> = Transformations.switchMap(_favAyah){ result ->
        val data = MediatorLiveData<Resource<List<MsFavAyah>>>()
        val local = quranRepository.getListFavAyah()
        data.value = Resource.loading(null)

        data.addSource(local){
            data.value = Resource.success(it)
        }

        return@switchMap data
    }
    fun getMsFavAyah() {
        this._favAyah.value = 0
    }

    private var _notifiedPrayer = MutableLiveData<Resource<List<NotifiedPrayer>>>()
    val notifiedPrayer: LiveData<Resource<List<NotifiedPrayer>>>
        get() = _notifiedPrayer
    fun syncNotifiedPrayer(msApi1: MsApi1) = viewModelScope.launch {
        _notifiedPrayer.postValue(Resource.loading(null))
        try {
            runIdlingResourceIncrement()
            if(IS_TESTING){
                prayerRepository.syncNotifiedPrayerTesting().let {
                    _notifiedPrayer.postValue(Resource.success(it))
                    runIdlingResourceDecrement()
                }
            }
            else {
                val response = prayerRepository.fetchPrayerApi(msApi1).await()
                val result = prayerRepository.getListNotifiedPrayerSync()

                if(response.statusResponse == "1"){
                    val currentDate = SimpleDateFormat("dd", Locale.getDefault()).format(Date())
                    val timings = response.data.find { obj -> obj.date.gregorian?.day == currentDate.toString() }?.timings

                    if(timings != null){
                        val prayers = createPrayerTime(timings)
                        prayers.forEach { prayer -> prayerRepository.updatePrayerTime(prayer.key, prayer.value) }
                        _notifiedPrayer.postValue(Resource.success(result))
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

    private var _setting = MutableLiveData<Int>()
    var msSetting: LiveData<Resource<MsSetting>> = Transformations.switchMap(_setting){
        val data = MediatorLiveData<Resource<MsSetting>>()
        val msSetting = prayerRepository.getMsSetting()

        data.value = Resource.loading(null)

        data.addSource(msSetting) {
            data.value = Resource.success(it)
        }

        return@switchMap data
    }
    fun getMsSetting(){
        this._setting.value = 0
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