package com.programmergabut.solatkuy.ui.fragmentquran

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceDecrement
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceIncrement
import kotlinx.coroutines.launch
import java.lang.Exception

class QuranFragmentViewModel @ViewModelInject constructor(val quranRepository: QuranRepository): ViewModel() {


    private var _allSurah = MutableLiveData<Resource<AllSurahResponse>>()
    val allSurah: LiveData<Resource<AllSurahResponse>>
        get() = _allSurah

    fun fetchAllSurah(){
        viewModelScope.launch {
            _allSurah.postValue(Resource.loading(null))

            try{
                runIdlingResourceIncrement()
                quranRepository.fetchAllSurah().let {
                    _allSurah.postValue(Resource.success(it))
                    runIdlingResourceDecrement()
                }
            }
            catch (ex: Exception){
                _allSurah.postValue(Resource.error("No internet connection", null))
                runIdlingResourceDecrement()
            }

        }
    }

    val staredSurah = quranRepository.getListFavSurah()

}