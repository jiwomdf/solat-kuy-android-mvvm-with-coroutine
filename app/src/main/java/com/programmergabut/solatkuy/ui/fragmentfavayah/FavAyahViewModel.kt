package com.programmergabut.solatkuy.ui.fragmentfavayah

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import kotlinx.coroutines.launch

class FavAyahViewModel @ViewModelInject constructor(val quranRepository: QuranRepository): ViewModel() {

    private var _favAyah = MutableLiveData<List<MsFavAyah>>()
    val favAyah: LiveData<List<MsFavAyah>>
        get() = _favAyah
    fun getMsFavAyah() {
        viewModelScope.launch {
            val result = quranRepository.getListFavAyah()
            _favAyah.postValue(result)
        }
    }

    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch {
        quranRepository.deleteFavAyah(msFavAyah)
    }

}