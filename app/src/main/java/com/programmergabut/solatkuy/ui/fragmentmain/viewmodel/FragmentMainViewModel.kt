package com.programmergabut.solatkuy.ui.fragmentmain.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.quransurahJson.QuranSurahApi
import com.programmergabut.solatkuy.util.Resource

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentMainViewModel(application: Application, private val repository: Repository): AndroidViewModel(application) {

    private var msApi1Param = MutableLiveData<MsApi1>()
    private var quranSurahID = MutableLiveData<String>()

    val msApi1Local = repository.getMsApi1()

    var quranSurah : MutableLiveData<Resource<QuranSurahApi>> = Transformations.switchMap(quranSurahID){
        repository.fetchQuranSurah(quranSurahID.value!!)
    } as MutableLiveData<Resource<QuranSurahApi>>

    val notifiedPrayer : MutableLiveData<Resource<List<NotifiedPrayer>>> = Transformations.switchMap(msApi1Param){
        repository.syncNotifiedPrayer(it)
    } as MutableLiveData<Resource<List<NotifiedPrayer>>>

    fun fetchPrayerApi(msApi1: MsApi1){
        this.msApi1Param.value = msApi1
    }

    fun fetchQuranSurah(nInSurah: String){
        this.quranSurahID.value = nInSurah
    }

    /*fun updateNotifiedPrayer(NotifiedPrayer: NotifiedPrayer){
        repository.updateNotifiedPrayer(NotifiedPrayer)
    }*/

    fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean) = repository.updatePrayerIsNotified(prayerName, isNotified)

}