package com.programmergabut.solatkuy.ui.fragmentquran

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.NetworkHelper
import kotlinx.coroutines.launch

class QuranFragmentViewModel constructor(val repository: Repository, val networkHelper: NetworkHelper): ViewModel() {


    private var _allSurah = MutableLiveData<Resource<AllSurahResponse>>()
    val allSurah: LiveData<Resource<AllSurahResponse>>
        get() = _allSurah

    fun fetchAllSurah(){
        viewModelScope.launch {
            _allSurah.postValue(Resource.loading(null))

            if (networkHelper.isNetworkConnected()) {
                repository.fetchAllSurah().let {
                    if (it.isSuccessful)
                        _allSurah.postValue(Resource.success(it.body()))
                    else
                        _allSurah.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }
            else
                _allSurah.postValue(Resource.error("No internet connection", null))
        }
    }

    private var _staredSurah = MutableLiveData<Resource<List<MsFavSurah>>>()
    val staredSurah: LiveData<Resource<List<MsFavSurah>>>
        get() = _staredSurah

    fun getStaredSurah() = viewModelScope.launch {

        _staredSurah.postValue(Resource.loading(null))

        repository.getMsFavSurah().let {
            _staredSurah.postValue(Resource.success(it))
        }
    }

    init {
        fetchAllSurah()
    }


}