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
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.EnumConfig.Companion.IS_TESTING
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceDecrement
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceIncrement
import kotlinx.coroutines.launch
import java.lang.Exception

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentMainViewModel @ViewModelInject constructor(
    val prayerRepository: PrayerRepository, val quranRepository: QuranRepository
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
                prayerRepository.syncNotifiedPrayer(msApi1).let {
                    _notifiedPrayer.postValue(Resource.success(it))
                    runIdlingResourceDecrement()
                }
            }
        }
        catch (e: Exception){
            _notifiedPrayer.postValue(Resource.error(e.message.toString(), null))
            runIdlingResourceDecrement()
        }
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

            runIdlingResourceIncrement()
            _prayer.postValue(Resource.loading(null))

            try{
                prayerRepository.fetchPrayerApi(msApi1).let {
                    _prayer.postValue(Resource.success(it))
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
        prayerRepository.getMsSetting()
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