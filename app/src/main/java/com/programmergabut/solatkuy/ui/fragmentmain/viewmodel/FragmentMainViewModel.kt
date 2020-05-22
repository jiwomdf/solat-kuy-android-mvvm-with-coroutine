package com.programmergabut.solatkuy.ui.fragmentmain.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.remote.remoteentity.quransurahJson.QuranSurahApi
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentMainViewModel(application: Application, private val repository: Repository): AndroidViewModel(application) {

    private var idMsApi1 = MutableLiveData<MsApi1>()
    private var idNInSurah = MutableLiveData<String>()

    val listNotifiedPrayer: LiveData<List<NotifiedPrayer>> = repository.mListPrayerLocal
    val msApi1Local: LiveData<MsApi1> = repository.mMsApi1

    var quranSurah : MutableLiveData<Resource<QuranSurahApi>> = Transformations.switchMap(idNInSurah){
        repository.fetchQuranSurah(idNInSurah.value!!)
    } as MutableLiveData<Resource<QuranSurahApi>>

    val notifiedPrayer : MutableLiveData<Resource<List<NotifiedPrayer>>> = Transformations.switchMap(idMsApi1){
        repository.syncNotifiedPrayer(it)
    } as MutableLiveData<Resource<List<NotifiedPrayer>>>

    fun fetchPrayerApi(msApi1: MsApi1){
        this.idMsApi1.value = msApi1
    }

    fun fetchQuranSurah(nInSurah: String){
        this.idNInSurah.value = nInSurah
    }

    fun updateNotifiedPrayer(NotifiedPrayer: NotifiedPrayer){
        repository.updateNotifiedPrayer(NotifiedPrayer)
    }

    fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = viewModelScope.launch {
        repository.updatePrayerIsNotified(prayerName, isNotified)
    }

}