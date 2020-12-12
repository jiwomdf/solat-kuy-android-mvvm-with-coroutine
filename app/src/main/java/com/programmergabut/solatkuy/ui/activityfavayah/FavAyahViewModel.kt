package com.programmergabut.solatkuy.ui.activityfavayah

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.QuranRepositoryImpl
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import kotlinx.coroutines.launch

class FavAyahViewModel @ViewModelInject constructor(val quranRepositoryImpl: QuranRepositoryImpl): ViewModel() {

    val favAyah = quranRepositoryImpl.getListFavAyah()

    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch {
        quranRepositoryImpl.deleteFavAyah(msFavAyah)
    }

}