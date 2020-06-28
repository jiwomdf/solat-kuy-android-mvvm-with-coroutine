package com.programmergabut.solatkuy.ui.fragmentmain

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.NetworkHelper
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentMainViewModel constructor(val repository: Repository, val networkHelper: NetworkHelper): ViewModel() {

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

    private var _msApi1 = MutableLiveData<Resource<MsApi1>>()
    val msApi1: LiveData<Resource<MsApi1>>
        get() = _msApi1

    fun getMsApi1() = viewModelScope.launch {
        _msApi1.postValue(Resource.loading(null))

        repository.getMsApi1().let {
            _msApi1.postValue(Resource.success(it))
        }
    }

    private var _msSetting = MutableLiveData<Resource<MsSetting>>()
    val msSetting: LiveData<Resource<MsSetting>>
        get() = _msSetting

    fun getMsSetting() = viewModelScope.launch {

        _msSetting.postValue(Resource.loading(null))

        repository.getMsSetting().let {
            _msSetting.postValue(Resource.success(it))
        }
    }

    private var _favAyah = MutableLiveData<Resource<List<MsFavAyah>>>()
    val favAyah: LiveData<Resource<List<MsFavAyah>>>
        get() = _favAyah

    fun getFavAyah() = viewModelScope.launch {
        _favAyah.postValue(Resource.loading(null))

        repository.getMsFavAyah().let {
            _favAyah.postValue(Resource.success(it))
        }
    }

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

    init {
        getMsSetting()
        getMsApi1()
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