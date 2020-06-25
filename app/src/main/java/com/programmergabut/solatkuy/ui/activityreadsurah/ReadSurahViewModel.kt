package com.programmergabut.solatkuy.ui.activityreadsurah

import android.app.Application
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch

class ReadSurahViewModel(application: Application, private val repository: Repository): AndroidViewModel(application) {

    private var surahID = MutableLiveData<Int>()
    private var favSurahID = MutableLiveData<Int>()
    private var ayahID = MutableLiveData<Int>()

    var selectedSurahAr : MutableLiveData<Resource<ReadSurahArResponse>> = Transformations.switchMap(surahID){
        repository.fetchReadSurahAr(it)
    } as MutableLiveData<Resource<ReadSurahArResponse>>

    var msFavAyahBySurahID: LiveData<Resource<List<MsFavAyah>>> = Transformations.switchMap(favSurahID){
        repository.getMsFavAyahBySurahID(it)
    }

    var msFavSurah = Transformations.switchMap(ayahID){
        repository.getMsFavSurahByID(it)
    }

    fun fetchQuranSurah(surahID: Int){
        this.surahID.value = surahID
    }

    fun fetchFavoriteData(surahID: Int){
        this.favSurahID.value = surahID
    }

    fun getFavSurah(ayahID: Int){
        this.ayahID.value = ayahID
    }

    fun insertFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { repository.insertFavAyah(msFavAyah) }
    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { repository.deleteFavAyah(msFavAyah) }

    fun insertFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { repository.insertFavSurah(msFavSurah) }
    fun deleteFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { repository.deleteFavSurah(msFavSurah) }
}