package com.programmergabut.solatkuy.ui.activityreadsurah

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.QuranRepositoryImpl
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceDecrement
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceIncrement
import kotlinx.coroutines.launch
import java.lang.Exception

class ReadSurahViewModel @ViewModelInject constructor(val quranRepositoryImpl: QuranRepositoryImpl): ViewModel() {

    lateinit var fetchedArSurah: ReadSurahArResponse
    private var _selectedSurahAr = MutableLiveData<Resource<ReadSurahArResponse>>()
    val selectedSurahAr: LiveData<Resource<ReadSurahArResponse>>
        get() = _selectedSurahAr

    fun fetchReadSurahAr(surahID: Int){
        viewModelScope.launch {
            _selectedSurahAr.postValue(Resource.loading(null))
            try {
                runIdlingResourceIncrement()
                val response = quranRepositoryImpl.fetchReadSurahAr(surahID).await()
                if(response.statusResponse == "1"){
                    fetchedArSurah = response
                    _selectedSurahAr.postValue(Resource.success(response))
                }
                else{
                    _selectedSurahAr.postValue(Resource.error(response.messageResponse, null))
                }
                runIdlingResourceDecrement()
            }
            catch (ex: Exception){
                val err = Resource.error(ex.message.toString(), null)
                _selectedSurahAr.postValue(err)
                runIdlingResourceDecrement()
            }
        }
    }

    private var favSurahID = MutableLiveData<Int>()
    private var selectedSurahId = 0
    private var lastSurah = 0
    private var lastAyah = 0
    val msFavAyahBySurahID: LiveData<Resource<List<MsFavAyah>>> = Transformations.switchMap(favSurahID) { result ->
        val data = MediatorLiveData<Resource<List<MsFavAyah>>>()
        val local = quranRepositoryImpl.getListFavAyahBySurahID(result)
        data.value = Resource.loading(null)

        data.addSource(local) {
            if(fetchedArSurah == null)
                return@addSource

            local.value?.forEach { ayah ->
                fetchedArSurah.data.ayahs.forEach out@{ remoteAyah ->
                    if (remoteAyah.numberInSurah == ayah.ayahID && selectedSurahId == ayah.surahID) {
                        remoteAyah.isFav = true
                        return@out
                    }
                }
            }

            fetchedArSurah.data.ayahs.forEach out@{ ayah ->
                if (lastSurah == selectedSurahId && lastAyah == ayah.numberInSurah) {
                    ayah.isLastRead = true
                    return@out
                }
            }

            data.value = Resource.success(it)
        }

        return@switchMap data
    }
    fun getListFavAyahBySurahID(surahID: Int, selectedSurahId: Int, lastSurah: Int, lastAyah: Int){
        this.favSurahID.value = surahID
        this.selectedSurahId = selectedSurahId
        this.lastSurah = lastSurah
        this.lastAyah = lastAyah
    }

    private var ayahID = MutableLiveData<Int>()
    var msFavSurah: LiveData<Resource<MsFavSurah>> = Transformations.switchMap(ayahID){
        quranRepositoryImpl.getFavSurahBySurahID(it)
    }
    fun getFavSurahBySurahID(ayahID: Int){
        this.ayahID.value = ayahID
    }

    fun insertFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { quranRepositoryImpl.insertFavAyah(msFavAyah) }
    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { quranRepositoryImpl.deleteFavAyah(msFavAyah) }

    fun insertFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { quranRepositoryImpl.insertFavSurah(msFavSurah) }
    fun deleteFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { quranRepositoryImpl.deleteFavSurah(msFavSurah) }
}