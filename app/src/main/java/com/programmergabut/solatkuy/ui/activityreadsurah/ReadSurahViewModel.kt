package com.programmergabut.solatkuy.ui.activityreadsurah

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArApi
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnApi
import com.programmergabut.solatkuy.util.Resource

class ReadSurahViewModel(application: Application, repository: Repository): AndroidViewModel(application) {

    private var surahID = MutableLiveData<String>()

    var selectedSurahAr : MutableLiveData<Resource<ReadSurahArApi>> = Transformations.switchMap(surahID){
        repository.fetchReadSurahAr(it)
    } as MutableLiveData<Resource<ReadSurahArApi>>

    fun fetchQuranSurah(nInSurah: String){
        this.surahID.value = nInSurah
    }

}