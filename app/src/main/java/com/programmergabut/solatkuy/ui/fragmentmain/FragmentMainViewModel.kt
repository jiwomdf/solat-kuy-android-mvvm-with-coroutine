package com.programmergabut.solatkuy.ui.fragmentmain

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentMainViewModel @ViewModelInject constructor(val repository: Repository): ViewModel() {

    val msApi1 = repository.getMsApi1()
    val favAyah = repository.getListFavAyah()

    private var _notifiedPrayer = MutableLiveData<Resource<List<NotifiedPrayer>>>()
    val notifiedPrayer: LiveData<Resource<List<NotifiedPrayer>>>
        get() = _notifiedPrayer

    fun syncNotifiedPrayer(msApi1: MsApi1) = viewModelScope.launch {
        _notifiedPrayer.postValue(Resource.loading(null))
        try {
            repository.syncNotifiedPrayer(msApi1).let {
                _notifiedPrayer.postValue(Resource.success(it))
            }
        }
        catch (e: Exception){
            _notifiedPrayer.postValue(Resource.error(e.message.toString(), null))
        }
    }

    private var _readSurahEn = MutableLiveData<Resource<ReadSurahEnResponse>>()
    val readSurahEn: LiveData<Resource<ReadSurahEnResponse>>
        get() = _readSurahEn

    fun fetchReadSurahEn(nInSurah: Int) = viewModelScope.launch{

        _readSurahEn.postValue(Resource.loading(null))

        try {
            repository.fetchReadSurahEn(nInSurah).let {
                _readSurahEn.postValue(Resource.success(it))
            }
        }
        catch (e: Exception){
            _readSurahEn.postValue(Resource.error(e.message.toString(), null))
        }
    }

    private var _setting = MutableLiveData<Int>()
    var msSetting: LiveData<Resource<MsSetting>> = Transformations.switchMap(_setting){
        repository.getMsSetting()
    }
    fun getMsSetting(ayahID: Int){
        this._setting.value = ayahID
    }

    /*fun updateNotifiedPrayer(NotifiedPrayer: NotifiedPrayer){
        repository.updateNotifiedPrayer(NotifiedPrayer)
    }*/

    fun updateMsApi1(msApi1: MsApi1) = viewModelScope.launch {
        repository.updateMsApi1(msApi1)
    }

    fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = viewModelScope.launch {
        repository.updatePrayerIsNotified(prayerName, isNotified)
    }

    fun updateIsUsingDBQuotes(isUsingDBQuotes: Boolean) = viewModelScope.launch {
        repository.updateIsUsingDBQuotes(isUsingDBQuotes)
    }

}