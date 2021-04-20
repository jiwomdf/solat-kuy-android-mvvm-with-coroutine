package com.programmergabut.solatkuy.ui.main.quran.readsurah

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.util.livedata.AbsentLiveData
import kotlinx.coroutines.launch

class ReadSurahViewModel @ViewModelInject constructor(val quranRepository: QuranRepository): ViewModel() {

    private var surahID2 = MutableLiveData<Int>()
    val selectedSurahAr = Transformations.switchMap(surahID2){
        if (it == null) {
            AbsentLiveData.create()
        } else {
            quranRepository.getReadSurahAr(it)
        }
    }
    fun getAyahsBySurahID(surahID: Int){
        this.surahID2.value = surahID
    }
    /* private var _selectedSurahAr = SingleLiveEvent<Resource<ReadSurahArResponse>>()
    val selectedSurahAr: LiveData<Resource<ReadSurahArResponse>> = _selectedSurahAr
    fun fetchReadSurahAr(surahID: Int){
        viewModelScope.launch {
            _selectedSurahAr.postValue(Resource.loading(null))
            try {
                runIdlingResourceIncrement()
                val readSurahAr = quranRepository.fetchReadSurahAr(surahID).await()
                val readSurahEn = quranRepository.fetchReadSurahEn(surahID).await()
                if(readSurahAr.responseStatus == "1" &&
                    readSurahEn.responseStatus == "1" &&
                    readSurahAr.data != null &&
                    readSurahEn.data != null
                ){
                    val listAyah = readSurahAr.data.ayahs.map { x -> Ayah(
                        x.hizbQuarter,
                        x.juz,
                        x.manzil,
                        x.number,
                        x.numberInSurah,
                        x.page,
                        x.ruku,
                        x.text,
                        "",
                        isFav = false,
                        isLastRead = false
                    )}
                    readSurahEn.data.ayahs.forEachIndexed { index, x ->
                        listAyah[index].textEn = x.text
                    }
                    readSurahAr.data.ayahs = listAyah
                    _selectedSurahAr.postValue(Resource.success(readSurahAr))
                }
                else{
                    _selectedSurahAr.postValue(Resource.error(null, readSurahAr.message))
                }
                runIdlingResourceDecrement()
            }
            catch (ex: Exception){
                val err = Resource.error(null, ex.message.toString())
                _selectedSurahAr.postValue(err)
                runIdlingResourceDecrement()
            }
        }
    } */

    /* fun getFavAyah(){
        if(selectedSurahAr.value?.data?.data?.ayahs == null){
            _selectedSurahAr.postValue(selectedSurahAr.value)
        } else {
            selectedSurahAr.value?.data?.data?.ayahs?.forEach { ayah ->
                if(ayah.isFav)
                    ayah.isFav = false
            }
            _selectedSurahAr.postValue(selectedSurahAr.value)
        }
    }

    fun getLastReadAyah(){
        if(selectedSurahAr.value?.data?.data?.ayahs == null){
            _selectedSurahAr.postValue(selectedSurahAr.value)
        } else {
            selectedSurahAr.value?.data?.data?.ayahs?.forEach { ayah ->
                if(ayah.isLastRead)
                    ayah.isLastRead = false
            }
            _selectedSurahAr.postValue(selectedSurahAr.value)
        }
    }

    private var _msFavAyahBySurahID = SingleLiveEvent<Resource<List<MsFavAyah>>>()
    val msFavAyahBySurahID: LiveData<Resource<List<MsFavAyah>>> = _msFavAyahBySurahID
    fun getListFavAyahBySurahID(surahID: Int, selectedSurahId: Int, lastSurah: Int, lastAyah: Int){
        viewModelScope.launch {
            val local = quranRepository.getListFavAyahBySurahID(surahID)
            if(selectedSurahAr.value?.data?.data?.ayahs == null){
                _msFavAyahBySurahID.postValue(Resource.success(local))
            } else {
                val fetchedArSurah = selectedSurahAr.value?.data?.data?.ayahs!!
                local?.forEach { ayah ->
                    fetchedArSurah.forEach out@{ remoteAyah ->
                        if (remoteAyah.numberInSurah == ayah.ayahID && selectedSurahId == ayah.surahID) {
                            remoteAyah.isFav = true
                            return@out
                        }
                    }
                }
                fetchedArSurah.forEach out@{ ayah ->
                    if (lastSurah == selectedSurahId && lastAyah == ayah.numberInSurah) {
                        ayah.isLastRead = true
                        return@out
                    }
                }
                _msFavAyahBySurahID.postValue(Resource.success(local))
            }
        }
    } */

    private var surahID = MutableLiveData<Int>()
    val favSurahBySurahID: LiveData<MsFavSurah?> = Transformations.switchMap(surahID) { ayahID ->
        quranRepository.observeFavSurahBySurahID(ayahID)
    }
    fun getFavSurahBySurahID(surahID: Int){
        this.surahID.value = surahID
    }

    fun insertFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { quranRepository.insertFavAyah(msFavAyah) }
    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { quranRepository.deleteFavAyah(msFavAyah) }
    fun insertFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { quranRepository.insertFavSurah(msFavSurah) }
    fun deleteFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { quranRepository.deleteFavSurah(msFavSurah) }
}