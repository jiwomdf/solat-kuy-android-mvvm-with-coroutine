package com.programmergabut.solatkuy.ui.fragmentmain.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentMainViewModel(application: Application, private val repository: Repository): AndroidViewModel(application) {

    private var msApi1Param = MutableLiveData<MsApi1>()
    private var quranSurahID = MutableLiveData<Int>()

    val msApi1Local = repository.getMsApi1()

    var readSurahEn : MutableLiveData<Resource<ReadSurahEnResponse>> = Transformations.switchMap(quranSurahID){
        repository.fetchReadSurahEn(quranSurahID.value!!)
    } as MutableLiveData<Resource<ReadSurahEnResponse>>

    val notifiedPrayer : MutableLiveData<Resource<List<NotifiedPrayer>>> = Transformations.switchMap(msApi1Param){
        repository.syncNotifiedPrayer(it)
    } as MutableLiveData<Resource<List<NotifiedPrayer>>>

    val msSetting = repository.getMsSetting()
    val favAyah = repository.getMsFavAyah()

    fun fetchPrayerApi(msApi1: MsApi1){
        this.msApi1Param.value = msApi1
    }

    fun fetchQuranSurah(nInSurah: Int){
        this.quranSurahID.value = nInSurah
    }

    /*fun updateNotifiedPrayer(NotifiedPrayer: NotifiedPrayer){
        repository.updateNotifiedPrayer(NotifiedPrayer)
    }*/

    fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = viewModelScope.launch {
        repository.updatePrayerIsNotified(prayerName, isNotified)
    }

    fun updateIsUsingDBQuotes(isUsingDBQuotes: Boolean) = viewModelScope.launch {
        repository.updateIsUsingDBQuotes(isUsingDBQuotes)
    }

}