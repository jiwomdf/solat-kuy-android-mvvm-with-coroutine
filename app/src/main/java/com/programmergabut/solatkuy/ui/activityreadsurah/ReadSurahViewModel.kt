package com.programmergabut.solatkuy.ui.activityreadsurah

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class ReadSurahViewModel @ViewModelInject constructor(val repository: Repository): ViewModel() {

    private var _selectedSurahAr = MutableLiveData<Resource<ReadSurahArResponse>>()
    val selectedSurahAr: LiveData<Resource<ReadSurahArResponse>>
        get() = _selectedSurahAr

    fun fetchReadSurahAr(surahID: Int){
        viewModelScope.launch {

            _selectedSurahAr.postValue(Resource.loading(null))

            try {
                repository.fetchReadSurahAr(surahID).let {
                    _selectedSurahAr.postValue(Resource.success(it))
                }
            }
            catch (ex: Exception){
                _selectedSurahAr.postValue(Resource.error(ex.message.toString(), null))
            }

        }
    }

    private var favSurahID = MutableLiveData<Int>()
    val msFavAyahBySurahID: LiveData<Resource<List<MsFavAyah>>> = Transformations.switchMap(favSurahID){
        repository.getListFavAyahBySurahID(it)
    }
    fun getListFavAyahBySurahID(surahID: Int){
        this.favSurahID.value = surahID
    }

    private var ayahID = MutableLiveData<Int>()
    var msFavSurah: LiveData<Resource<MsFavSurah>> = Transformations.switchMap(ayahID){
        repository.getFavSurahBySurahID(it)
    }
    fun getFavSurahBySurahID(ayahID: Int){
        this.ayahID.value = ayahID
    }


    fun insertFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { repository.insertFavAyah(msFavAyah) }
    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { repository.deleteFavAyah(msFavAyah) }

    fun insertFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { repository.insertFavSurah(msFavSurah) }
    fun deleteFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { repository.deleteFavSurah(msFavSurah) }
}