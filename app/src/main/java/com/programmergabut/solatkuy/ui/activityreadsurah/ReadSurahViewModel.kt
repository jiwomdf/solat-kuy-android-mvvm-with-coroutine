package com.programmergabut.solatkuy.ui.activityreadsurah

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.NetworkHelper
import kotlinx.coroutines.launch

class ReadSurahViewModel constructor(val repository: Repository, val networkHelper: NetworkHelper): ViewModel() {

    private var _selectedSurahAr = MutableLiveData<Resource<ReadSurahArResponse>>()
    val selectedSurahAr: LiveData<Resource<ReadSurahArResponse>>
        get() = _selectedSurahAr

    fun fetchQuranSurah(surahID: Int){
        viewModelScope.launch {

            _selectedSurahAr.postValue(Resource.loading(null))

            if (networkHelper.isNetworkConnected()) {
                repository.fetchReadSurahAr(surahID).let {
                    _selectedSurahAr.postValue(Resource.success(it))
                }
            }
            else
                _selectedSurahAr.postValue(Resource.error("No internet connection", null))
        }
    }

    private var _msFavAyahBySurahID = MutableLiveData<Resource<List<MsFavAyah>>>()
    val msFavAyahBySurahID: LiveData<Resource<List<MsFavAyah>>>
        get() = _msFavAyahBySurahID

    fun getFavoriteData(surahID: Int){
        viewModelScope.launch {
            _msFavAyahBySurahID.postValue(Resource.loading(null))

            repository.getMsFavAyahBySurahID(surahID).let {
                _msFavAyahBySurahID.postValue(Resource.success(it))
            }
        }
    }

    private var _msFavSurah = MutableLiveData<Resource<MsFavSurah>>()
    val msFavSurah: LiveData<Resource<MsFavSurah>>
        get() = _msFavSurah

    fun getFavSurah(ayahID: Int){
        viewModelScope.launch {
            _msFavSurah.postValue(Resource.loading(null))

            repository.getMsFavSurahByID(ayahID).let {
                _msFavSurah.postValue(Resource.success(it))
            }
        }
    }

    fun insertFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { repository.insertFavAyah(msFavAyah) }
    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { repository.deleteFavAyah(msFavAyah) }

    fun insertFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { repository.insertFavSurah(msFavSurah) }
    fun deleteFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { repository.deleteFavSurah(msFavSurah) }
}