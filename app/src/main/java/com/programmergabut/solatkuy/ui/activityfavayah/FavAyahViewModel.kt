package com.programmergabut.solatkuy.ui.activityfavayah

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch

class FavAyahViewModel constructor(val repository: Repository): ViewModel() {

    private var _favAyah = MutableLiveData<Resource<List<MsFavAyah>>>()
    val favAyah: LiveData<Resource<List<MsFavAyah>>>
        get() = _favAyah


    fun getFavAyah() = viewModelScope.launch {
        _favAyah.postValue(Resource.loading(null))

        repository.getMsFavAyah().let {
            _favAyah.postValue(Resource.success(it))
        }
    }

    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { repository.deleteFavAyah(msFavAyah) }

}