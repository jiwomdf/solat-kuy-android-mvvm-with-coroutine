package com.programmergabut.solatkuy.ui.activityfavayah

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch

class FavAyahViewModel @ViewModelInject constructor(val quranRepository: QuranRepository): ViewModel() {

    fun favAyah(): LiveData<Resource<List<MsFavAyah>>> {
        val data = MediatorLiveData<Resource<List<MsFavAyah>>>()
        val result = quranRepository.getListFavAyah()
        data.value = Resource.loading(null)

        data.addSource(result){
            data.value = Resource.success(it)
        }

        return data
    }

    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch {
        quranRepository.deleteFavAyah(msFavAyah)
    }

}