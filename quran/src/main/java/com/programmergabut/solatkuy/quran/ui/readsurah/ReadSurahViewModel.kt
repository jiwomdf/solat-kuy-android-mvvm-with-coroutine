package com.programmergabut.solatkuy.quran.ui.readsurah

import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.repository.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.util.livedata.AbsentLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReadSurahViewModel @Inject constructor(val quranRepository: QuranRepository): ViewModel() {

    private var surahID = MutableLiveData<Int>()
    val favSurahBySurahID: LiveData<MsFavSurah?> = Transformations.switchMap(surahID) { ayahID ->
        quranRepository.observeFavSurahBySurahID(ayahID)
    }
    val selectedSurah = Transformations.switchMap(surahID){
        if (it == null) {
            AbsentLiveData.create()
        } else {
            quranRepository.getAyahBySurahID(it)
        }
    }
    fun getSelectedSurah(surahID: Int){
        this.surahID.value = surahID
    }

    fun insertFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { quranRepository.insertFavSurah(msFavSurah) }
    fun deleteFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { quranRepository.deleteFavSurah(msFavSurah) }
}