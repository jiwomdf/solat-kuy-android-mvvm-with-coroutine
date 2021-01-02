package com.programmergabut.solatkuy.ui.activitymain.fragmentquran

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceDecrement
import com.programmergabut.solatkuy.util.helper.RunIdlingResourceHelper.Companion.runIdlingResourceIncrement
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class QuranFragmentViewModel @ViewModelInject constructor(private val quranRepository: QuranRepository): ViewModel() {

    private lateinit var allSurahSync: List<Data>
    private var _allSurah = MutableLiveData<Resource<List<Data>>>()
    val allSurah: LiveData<Resource<List<Data>>>
        get() = _allSurah

    fun fetchAllSurah(){
        viewModelScope.launch {
            _allSurah.postValue(Resource.loading(null))
            try{
                runIdlingResourceIncrement()
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
                    allSurahSync = response.data
                    _allSurah.postValue(Resource.success(mappedResponse))
                }
                else{
                    _allSurah.postValue(Resource.error(response.messageResponse, null))
                }
                runIdlingResourceDecrement()
            }
            catch (ex: Exception){
                _allSurah.postValue(Resource.error(ex.message.toString(), null))
                runIdlingResourceDecrement()
            }
        }
    }

    fun getSurahByJuzz(juzz: Int): List<Data>{
        if(allSurahSync == null)
            return listOf()

        val allSurahDatas = allSurahSync
        return when(juzz){
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
    }

    val staredSurah = quranRepository.getListFavSurah()

}