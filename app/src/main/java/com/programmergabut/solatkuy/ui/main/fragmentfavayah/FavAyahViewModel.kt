package com.programmergabut.solatkuy.ui.main.fragmentfavayah

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import kotlinx.coroutines.launch

class FavAyahViewModel @ViewModelInject constructor(val quranRepository: QuranRepository): ViewModel() {

    val favAyah: LiveData<List<MsFavAyah>> = quranRepository.getListFavAyah()

    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch {
        quranRepository.deleteFavAyah(msFavAyah)
    }

}