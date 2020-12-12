package com.programmergabut.solatkuy.ui.activityreadsurah

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.QuranRepositoryImpl
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceDecrement
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceIncrement
import kotlinx.coroutines.launch
import java.lang.Exception

class ReadSurahViewModel @ViewModelInject constructor(val quranRepositoryImpl: QuranRepositoryImpl): ViewModel() {

    private var _selectedSurahAr = MutableLiveData<Resource<ReadSurahArResponse>>()
    val selectedSurahAr: LiveData<Resource<ReadSurahArResponse>>
        get() = _selectedSurahAr

    fun fetchReadSurahAr(surahID: Int){
        viewModelScope.launch {

            _selectedSurahAr.postValue(Resource.loading(null))

            try {
                runIdlingResourceIncrement()
                quranRepositoryImpl.fetchReadSurahAr(surahID).let {
                    _selectedSurahAr.postValue(Resource.success(it))
                    runIdlingResourceDecrement()
                }
            }
            catch (ex: Exception){
                runIdlingResourceDecrement()

                val err = Resource.error(ex.message.toString(), null)
                _selectedSurahAr.postValue(err)
            }
        }
    }

    private var favSurahID = MutableLiveData<Int>()
    val msFavAyahBySurahID: LiveData<Resource<List<MsFavAyah>>> = Transformations.switchMap(favSurahID){
        quranRepositoryImpl.getListFavAyahBySurahID(it)
    }
    fun getListFavAyahBySurahID(surahID: Int){
        this.favSurahID.value = surahID
    }

    private var ayahID = MutableLiveData<Int>()
    var msFavSurah: LiveData<Resource<MsFavSurah>> = Transformations.switchMap(ayahID){
        quranRepositoryImpl.getFavSurahBySurahID(it)
    }
    fun getFavSurahBySurahID(ayahID: Int){
        this.ayahID.value = ayahID
    }

    fun insertFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { quranRepositoryImpl.insertFavAyah(msFavAyah) }
    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { quranRepositoryImpl.deleteFavAyah(msFavAyah) }

    fun insertFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { quranRepositoryImpl.insertFavSurah(msFavSurah) }
    fun deleteFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { quranRepositoryImpl.deleteFavSurah(msFavSurah) }
}