package com.programmergabut.solatkuy.ui.fragmentquran

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahApi
import com.programmergabut.solatkuy.util.Resource

class QuranFragmentViewModel(application: Application, private val repository: Repository): AndroidViewModel(application) {

    private var updateAllSurah = MutableLiveData<String>()

    var allSurah : MutableLiveData<Resource<AllSurahApi>> =  Transformations.switchMap(updateAllSurah){
        repository.fetchAllSurah()
    } as MutableLiveData<Resource<AllSurahApi>>


    fun fetchAllSurah(updateAllSurah: String){
        this.updateAllSurah.value = updateAllSurah
    }
}