package com.programmergabut.solatkuy.ui.fragmentquran

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.QuranRepositoryImpl
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceDecrement
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceIncrement
import kotlinx.coroutines.launch
import java.lang.Exception

class QuranFragmentViewModel @ViewModelInject constructor(val quranRepositoryImpl: QuranRepository): ViewModel() {

    var allSurahStatus = MutableLiveData<Resource<Unit>>()
    private var _allSurah = AllSurahResponse(0, listOf(), "")
    val allSurah: AllSurahResponse
        get() = _allSurah

    fun fetchAllSurah(){
        viewModelScope.launch {
            allSurahStatus.postValue(Resource.loading(null))
            try{
                runIdlingResourceIncrement()
                quranRepositoryImpl.fetchAllSurah().let {
                    _allSurah = it
                    allSurahStatus.postValue(Resource.success(null))
                    runIdlingResourceDecrement()
                }
            }
            catch (ex: Exception){
                runIdlingResourceDecrement()
                allSurahStatus.postValue(Resource.error(ex.message.toString(), null))
            }
        }
    }

    val staredSurah = quranRepositoryImpl.getListFavSurah()

}