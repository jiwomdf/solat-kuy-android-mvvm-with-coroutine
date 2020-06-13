package com.programmergabut.solatkuy.ui.activityreadsurah

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArApi
import com.programmergabut.solatkuy.util.Resource

class ReadSurahViewModel(application: Application, private val repository: Repository): AndroidViewModel(application) {

    private var surahID = MutableLiveData<Int>()
    private var favSurahID = MutableLiveData<Int>()
    private var ayahID = MutableLiveData<Int>()

    var selectedSurahAr : MutableLiveData<Resource<ReadSurahArApi>> = Transformations.switchMap(surahID){
        repository.fetchReadSurahAr(it)
    } as MutableLiveData<Resource<ReadSurahArApi>>

    var msFavAyahBySurahID: LiveData<List<MsFavAyah>> = Transformations.switchMap(favSurahID){
        repository.getMsFavAyahBySurahID(it)
    }

    var msFavSurah = Transformations.switchMap(ayahID){
        repository.getMsFavSurahByUD(it)
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

    fun insertFavAyah(msFavAyah: MsFavAyah) = repository.insertFavAyah(msFavAyah)
    fun deleteFavAyah(msFavAyah: MsFavAyah) = repository.deleteFavAyah(msFavAyah)

    fun insertFavSurah(msFavSurah: MsFavSurah) = repository.insertFavSurah(msFavSurah)
    fun deleteFavSurah(msFavSurah: MsFavSurah) = repository.deleteFavSurah(msFavSurah)
}