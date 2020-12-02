package com.programmergabut.solatkuy.ui.fragmentquran

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.programmergabut.solatkuy.data.PrayerRepository
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.util.EspressoIdlingResource
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.launch
import java.lang.Exception

class QuranFragmentViewModel @ViewModelInject constructor(val quranRepository: QuranRepository): ViewModel() {


    private var _allSurah = MutableLiveData<Resource<AllSurahResponse>>()
    val allSurah: LiveData<Resource<AllSurahResponse>>
        get() = _allSurah

    fun fetchAllSurah(){
        viewModelScope.launch {
            _allSurah.postValue(Resource.loading(null))

            EspressoIdlingResource.increment()
            try{
                quranRepository.fetchAllSurah().let {
                    _allSurah.postValue(Resource.success(it))
                    EspressoIdlingResource.decrement()
                }
            }
            catch (ex: Exception){
                _allSurah.postValue(Resource.error("No internet connection", null))
                EspressoIdlingResource.decrement()
            }

        }
    }

    val staredSurah = quranRepository.getListFavSurah()

}