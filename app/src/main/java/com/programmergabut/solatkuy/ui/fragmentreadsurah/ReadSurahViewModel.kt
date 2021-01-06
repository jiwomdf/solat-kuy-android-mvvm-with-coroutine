package com.programmergabut.solatkuy.ui.fragmentreadsurah

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.livedata.SingleLiveEvent
import com.programmergabut.solatkuy.util.idlingresource.RunIdlingResourceHelper.Companion.runIdlingResourceDecrement
import com.programmergabut.solatkuy.util.idlingresource.RunIdlingResourceHelper.Companion.runIdlingResourceIncrement
import kotlinx.coroutines.launch
import java.lang.Exception

class ReadSurahViewModel @ViewModelInject constructor(val quranRepository: QuranRepository): ViewModel() {

    lateinit var fetchedArSurah: ReadSurahArResponse
    private var _selectedSurahAr = SingleLiveEvent<Resource<ReadSurahArResponse>>()
    val selectedSurahAr: LiveData<Resource<ReadSurahArResponse>>
        get() = _selectedSurahAr

    fun fetchReadSurahAr(surahID: Int){
        viewModelScope.launch {
            _selectedSurahAr.postValue(Resource.loading(null))
            try {
                runIdlingResourceIncrement()
                val readSurahAr = quranRepository.fetchReadSurahAr(surahID).await()
                val readSurahEn = quranRepository.fetchReadSurahEn(surahID).await()
                if(readSurahAr.statusResponse == "1" && readSurahEn.statusResponse == "1" &&
                    readSurahAr.data != null && readSurahEn.data != null){
                    fetchedArSurah = readSurahAr

                    val listAyah: MutableList<Ayah>
                    listAyah = readSurahAr.data.ayahs.map { x -> Ayah(
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
                    )} as MutableList<Ayah>

                    readSurahEn.data.ayahs.forEachIndexed { index, x ->
                        listAyah[index].textEn = x.text
                    }

                    readSurahAr.data.ayahs = listAyah
                    _selectedSurahAr.postValue(Resource.success(readSurahAr))
                }
                else if(readSurahEn.statusResponse != "1"){
                    _selectedSurahAr.postValue(Resource.error(readSurahEn.messageResponse, null))
                }
                else{
                    _selectedSurahAr.postValue(Resource.error(readSurahAr.messageResponse, null))
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

    private var _msFavAyahBySurahID = SingleLiveEvent<Resource<List<MsFavAyah>>>()
    val msFavAyahBySurahID: LiveData<Resource<List<MsFavAyah>>>
        get() = _msFavAyahBySurahID
    fun getListFavAyahBySurahID(surahID: Int, selectedSurahId: Int, lastSurah: Int, lastAyah: Int){
        viewModelScope.launch {
            val local = quranRepository.getListFavAyahBySurahID(surahID)

            if(fetchedArSurah == null)
                return@launch

            local.forEach { ayah ->
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

            _msFavAyahBySurahID.postValue(Resource.success(local))
        }
    }

    private var _msFavSurah = MutableLiveData<MsFavSurah>()
    val msFavSurah: LiveData<MsFavSurah>
        get() =  _msFavSurah
    fun getFavSurahBySurahID(ayahID: Int){
        viewModelScope.launch {
            val result = quranRepository.getFavSurahBySurahID(ayahID)
            _msFavSurah.postValue(result)
        }
    }

    fun insertFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { quranRepository.insertFavAyah(msFavAyah) }
    fun deleteFavAyah(msFavAyah: MsFavAyah) = viewModelScope.launch { quranRepository.deleteFavAyah(msFavAyah) }

    fun insertFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { quranRepository.insertFavSurah(msFavSurah) }
    fun deleteFavSurah(msFavSurah: MsFavSurah) = viewModelScope.launch { quranRepository.deleteFavSurah(msFavSurah) }
}