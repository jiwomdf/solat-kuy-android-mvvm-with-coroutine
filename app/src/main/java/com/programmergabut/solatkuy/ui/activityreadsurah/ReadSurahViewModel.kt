package com.programmergabut.solatkuy.ui.activityreadsurah

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceDecrement
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceIncrement
import kotlinx.coroutines.launch
import java.lang.Exception

class ReadSurahViewModel @ViewModelInject constructor(val quranRepository: QuranRepository): ViewModel() {

    private var _selectedSurahAr = MutableLiveData<Resource<ReadSurahArResponse>>()
    val selectedSurahAr: LiveData<Resource<ReadSurahArResponse>>
        get() = _selectedSurahAr

    fun fetchReadSurahAr(surahID: Int){
        viewModelScope.launch {

            _selectedSurahAr.postValue(Resource.loading(null))

            try {
                runIdlingResourceIncrement()
                quranRepository.fetchReadSurahAr(surahID).let {
                    _selectedSurahAr.postValue(Resource.success(it))
                    runIdlingResourceDecrement()
                }
            }
            catch (ex: Exception){
                runIdlingResourceDecrement()
                _selectedSurahAr.postValue(Resource.error(ex.message.toString(), null))
            }

        }
    }

    private var favSurahID = MutableLiveData<Int>()
    val msFavAyahBySurahID: LiveData<Resource<List<MsFavAyah>>> = Transformations.switchMap(favSurahID){
        quranRepository.getListFavAyahBySurahID(it)
    }
    fun getListFavAyahBySurahID(surahID: Int){
        this.favSurahID.value = surahID
    }

    private var ayahID = MutableLiveData<Int>()
    var msFavSurah: LiveData<Resource<MsFavSurah>> = Transformations.switchMap(ayahID){
        quranRepository.getFavSurahBySurahID(it)
    }
    fun getFavSurahBySurahID(ayahID: Int){
        this.ayahID.value = ayahID
    }

    fun insertFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { quranRepository.insertFavAyah(msFavAyah) }
    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { quranRepository.deleteFavAyah(msFavAyah) }

    fun insertFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { quranRepository.insertFavSurah(msFavSurah) }
    fun deleteFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { quranRepository.deleteFavSurah(msFavSurah) }
}