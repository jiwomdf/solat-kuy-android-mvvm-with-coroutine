package com.programmergabut.solatkuy.ui.main.fragmentquran

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.livedata.SingleLiveEvent
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

const val SAVED_ALL_SURAH_NULL = "Saved all surah null"
class QuranFragmentViewModel @ViewModelInject constructor(private val quranRepository: QuranRepository): ViewModel() {

    val staredSurah = quranRepository.observeListFavSurah()

    private var isFetchingAllSurahFinish = false
    var savedAllSurah: List<Data>? = null
    private var _allSurah = SingleLiveEvent<Resource<List<Data>?>>()
    val allSurah: LiveData<Resource<List<Data>?>>
        get() = _allSurah
    fun fetchAllSurah(){
        viewModelScope.launch {
            _allSurah.postValue(Resource.loading(null))
            try{
                val response = quranRepository.fetchAllSurah().await()
                if(response.statusResponse == "1"){
                    val mappedResponse = response.data.map { surah ->
                        Data(
                            surah.englishName,
                            surah.englishName.toLowerCase(Locale.getDefault()).replace("-", " "),
                            surah.englishNameTranslation,
                            surah.name,
                            surah.number,
                            surah.numberOfAyahs,
                            surah.revelationType
                        )
                    }
                    if(savedAllSurah == null){
                        savedAllSurah = mappedResponse
                    }
                    _allSurah.postValue(Resource.success(data = mappedResponse))
                    isFetchingAllSurahFinish = true
                }
                else{
                    _allSurah.postValue(Resource.error(null, response.messageResponse))
                }
            }
            catch (ex: Exception){
                _allSurah.postValue(Resource.error(null, ex.message.toString()))
            }
        }
    }

    fun getSurahBySeach(stringName: String){
        if(!isFetchingAllSurahFinish){
            return
        }
        if(savedAllSurah == null){
            _allSurah.postValue(Resource.error(null, SAVED_ALL_SURAH_NULL))
            return
        }
        val newData = savedAllSurah?.filter { surah -> surah.englishNameLowerCase!!.contains(stringName) }
        _allSurah.postValue(Resource.success(data = newData ?: emptyList()))
    }

    fun getSurahByJuzz(juzz: Int){
        if(!isFetchingAllSurahFinish){
            return
        }
        if(savedAllSurah == null){
            _allSurah.postValue(Resource.error(null, SAVED_ALL_SURAH_NULL))
            return
        }
        val allSurahDatas = savedAllSurah!!
        val rangedSurahByJuzz: List<Data>?
        rangedSurahByJuzz = when(juzz){
            0 -> allSurahDatas
            1 -> allSurahDatas.filter { x -> x.number in 1..2 }
            2 -> allSurahDatas.filter { x -> x.number == 2 }
            3 -> allSurahDatas.filter { x -> x.number in 2..3 }
            4 -> allSurahDatas.filter { x -> x.number in 3..4 }
            5 -> allSurahDatas.filter { x -> x.number == 4 }
            6 -> allSurahDatas.filter { x -> x.number in 4..5 }
            7 -> allSurahDatas.filter { x -> x.number in 5..6 }
            8 -> allSurahDatas.filter { x -> x.number in 6..7 }
            9 -> allSurahDatas.filter { x -> x.number in 7..8 }
            10 -> allSurahDatas.filter { x -> x.number in 8..9 }
            11 -> allSurahDatas.filter { x -> x.number in 9..11 }
            12 -> allSurahDatas.filter { x -> x.number in 11..12 }
            13 -> allSurahDatas.filter { x -> x.number in 12..14 }
            14 -> allSurahDatas.filter { x -> x.number in 15..16 }
            15 -> allSurahDatas.filter { x -> x.number in 17..18 }
            16 -> allSurahDatas.filter { x -> x.number in 18..20 }
            17 -> allSurahDatas.filter { x -> x.number in 21..22 }
            18 -> allSurahDatas.filter { x -> x.number in 23..25 }
            19 -> allSurahDatas.filter { x -> x.number in 25..27 }
            20 -> allSurahDatas.filter { x -> x.number in 27..29 }
            21 -> allSurahDatas.filter { x -> x.number in 29..33 }
            22 -> allSurahDatas.filter { x -> x.number in 33..36 }
            23 -> allSurahDatas.filter { x -> x.number in 36..38 }
            24 -> allSurahDatas.filter { x -> x.number in 39..41 }
            25 -> allSurahDatas.filter { x -> x.number in 41..45 }
            26 -> allSurahDatas.filter { x -> x.number in 46..51 }
            27 -> allSurahDatas.filter { x -> x.number in 51..57 }
            28 -> allSurahDatas.filter { x -> x.number in 58..66 }
            29 -> allSurahDatas.filter { x -> x.number in 67..77 }
            30 -> allSurahDatas.filter { x -> x.number in 78..144 }
            else -> allSurahDatas
        }
        _allSurah.postValue(Resource.success(data = rangedSurahByJuzz))
    }

}