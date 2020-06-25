package com.programmergabut.solatkuy.ui.activityfavayah

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import kotlinx.coroutines.launch

class FavAyahViewModel @ViewModelInject constructor(val repository: Repository): ViewModel() {

    val favAyah = repository.getMsFavAyah()

    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { repository.deleteFavAyah(msFavAyah) }

}