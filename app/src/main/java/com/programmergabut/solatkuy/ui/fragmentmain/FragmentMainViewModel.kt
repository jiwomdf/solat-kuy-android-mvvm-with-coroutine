package com.programmergabut.solatkuy.ui.fragmentmain

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.NetworkHelper
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentMainViewModel @ViewModelInject constructor(val repository: Repository, val networkHelper: NetworkHelper): ViewModel() {

    private var _notifiedPrayer = MutableLiveData<Resource<List<NotifiedPrayer>>>()
    val notifiedPrayer: LiveData<Resource<List<NotifiedPrayer>>>
        get() = _notifiedPrayer

    fun fetchNotifiedPrayer(msApi1: MsApi1){
        viewModelScope.launch {

            _notifiedPrayer.postValue(Resource.loading(null))

            if (networkHelper.isNetworkConnected()) {
                repository.syncNotifiedPrayer(msApi1).let {
                    _notifiedPrayer.postValue(Resource.success(it))
                }
            }
            else
                _notifiedPrayer.postValue(Resource.error("No internet connection", null))
        }
    }

    val msApi1 = repository.getMsApi1()

    private var _setting = MutableLiveData<Int>()
    var msSetting = Transformations.switchMap(_setting){
        repository.getMsSetting()
    }
    fun fetchMsSetting(ayahID: Int){
        this._setting.value = ayahID
    }

    val favAyah = repository.getListFavAyah()

    private var _readSurahEn = MutableLiveData<Resource<ReadSurahEnResponse>>()
    val readSurahEn: LiveData<Resource<ReadSurahEnResponse>>
        get() = _readSurahEn

    fun fetchQuranSurah(nInSurah: Int){
        viewModelScope.launch{
            _readSurahEn.postValue(Resource.loading(null))

            if (networkHelper.isNetworkConnected()) {
                repository.fetchReadSurahEn(nInSurah).let {
                    if (it.isSuccessful)
                        _readSurahEn.postValue(Resource.success(it.body()))
                    else
                        _readSurahEn.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
            else
                _readSurahEn.postValue(Resource.error("No internet connection", null))
        }
    }


    /*fun updateNotifiedPrayer(NotifiedPrayer: NotifiedPrayer){
        repository.updateNotifiedPrayer(NotifiedPrayer)
    }*/

    fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = viewModelScope.launch {
        repository.updatePrayerIsNotified(prayerName, isNotified)
    }

    fun updateIsUsingDBQuotes(isUsingDBQuotes: Boolean) = viewModelScope.launch {
        repository.updateIsUsingDBQuotes(isUsingDBQuotes)
    }

}