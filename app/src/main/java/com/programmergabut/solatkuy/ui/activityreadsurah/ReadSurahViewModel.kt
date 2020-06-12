package com.programmergabut.solatkuy.ui.activityreadsurah

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArApi
import com.programmergabut.solatkuy.util.Resource

class ReadSurahViewModel(application: Application, private val repository: Repository): AndroidViewModel(application) {

    private var surahID = MutableLiveData<Int>()
    private var ayahID = MutableLiveData<Int>()

    var selectedSurahAr : MutableLiveData<Resource<ReadSurahArApi>> = Transformations.switchMap(surahID){
        repository.fetchReadSurahAr(it)
    } as MutableLiveData<Resource<ReadSurahArApi>>

    var msFavAyahByID: LiveData<List<MsFavAyah>> = Transformations.switchMap(surahID){
        repository.getMsFavAyahByID(it)
    }

    var isFavoriteAyah: LiveData<Boolean> = Transformations.switchMap(ayahID){
        repository.isFavoriteAyah(it, surahID.value?.toInt()!!)
    }

    fun fetchQuranSurah(nInSurah: Int){
        this.surahID.value = nInSurah
    }

    fun isFavAyah(ayahID: Int){
        this.ayahID.value = ayahID
    }

    fun insertFavAyah(msFavAyah: MsFavAyah) = repository.insertFavAyah(msFavAyah)

}