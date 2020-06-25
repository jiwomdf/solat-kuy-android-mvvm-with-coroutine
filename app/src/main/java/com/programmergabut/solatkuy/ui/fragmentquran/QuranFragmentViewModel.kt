package com.programmergabut.solatkuy.ui.fragmentquran

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.util.Resource

class QuranFragmentViewModel @ViewModelInject constructor(val repository: Repository): ViewModel() {

    private var updateAllSurah = MutableLiveData<String>()


    var allSurah : MutableLiveData<Resource<AllSurahResponse>> =  Transformations.switchMap(updateAllSurah){
        repository.fetchAllSurah()
    } as MutableLiveData<Resource<AllSurahResponse>>

    var staredSurah: LiveData<Resource<List<MsFavSurah>>> =  repository.getMsFavSurah()

    fun fetchAllSurah(updateAllSurah: String){
        this.updateAllSurah.value = updateAllSurah
    }


}