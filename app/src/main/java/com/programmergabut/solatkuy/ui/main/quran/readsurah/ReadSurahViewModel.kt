package com.programmergabut.solatkuy.ui.main.quran.readsurah

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.util.livedata.AbsentLiveData
import kotlinx.coroutines.launch

class ReadSurahViewModel @ViewModelInject constructor(val quranRepository: QuranRepository): ViewModel() {

    private var surahID = MutableLiveData<Int>()
    val favSurahBySurahID: LiveData<MsFavSurah?> = Transformations.switchMap(surahID) { ayahID ->
        quranRepository.observeFavSurahBySurahID(ayahID)
    }
    val selectedSurah = Transformations.switchMap(surahID){
        if (it == null) {
            AbsentLiveData.create()
        } else {
            quranRepository.getSelectedSurah(it)
        }
    }
    fun getSelectedSurah(surahID: Int){
        this.surahID.value = surahID
    }

    fun insertFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { quranRepository.insertFavAyah(msFavAyah) }
    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { quranRepository.deleteFavAyah(msFavAyah) }
    fun insertFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { quranRepository.insertFavSurah(msFavSurah) }
    fun deleteFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { quranRepository.deleteFavSurah(msFavSurah) }
}