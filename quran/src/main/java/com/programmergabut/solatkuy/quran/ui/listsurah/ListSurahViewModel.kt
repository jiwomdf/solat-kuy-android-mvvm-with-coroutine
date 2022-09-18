package com.programmergabut.solatkuy.quran.ui.listsurah

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.programmergabut.solatkuy.data.repository.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsSurah
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.livedata.AbsentLiveData
import java.util.*
import javax.inject.Inject

class ListSurahViewModel @Inject constructor(private val quranRepository: QuranRepository): ViewModel() {

    val staredSurah = quranRepository.observeListFavSurah()

    private var isAyahsCalled = MutableLiveData(false)
    val allSurah: LiveData<Resource<List<MsSurah>>> = Transformations.switchMap(isAyahsCalled){ isAyahsCalled ->
        if (!isAyahsCalled) {
            AbsentLiveData.create()
        } else {
            quranRepository.getAllSurah()
        }
    }
    fun getAllSurah(value: Boolean = true){
        this.isAyahsCalled.value = value
    }

    fun getSurahByJuzz(juzz: Int): List<MsSurah> {
        allSurah.value?.data?.let { allSurah ->
            return when(juzz){
                0 -> allSurah
                1 -> allSurah.filter { x -> x.number in 1..2 }
                2 -> allSurah.filter { x -> x.number == 2 }
                3 -> allSurah.filter { x -> x.number in 2..3 }
                4 -> allSurah.filter { x -> x.number in 3..4 }
                5 -> allSurah.filter { x -> x.number == 4 }
                6 -> allSurah.filter { x -> x.number in 4..5 }
                7 -> allSurah.filter { x -> x.number in 5..6 }
                8 -> allSurah.filter { x -> x.number in 6..7 }
                9 -> allSurah.filter { x -> x.number in 7..8 }
                10 -> allSurah.filter { x -> x.number in 8..9 }
                11 -> allSurah.filter { x -> x.number in 9..11 }
                12 -> allSurah.filter { x -> x.number in 11..12 }
                13 -> allSurah.filter { x -> x.number in 12..14 }
                14 -> allSurah.filter { x -> x.number in 15..16 }
                15 -> allSurah.filter { x -> x.number in 17..18 }
                16 -> allSurah.filter { x -> x.number in 18..20 }
                17 -> allSurah.filter { x -> x.number in 21..22 }
                18 -> allSurah.filter { x -> x.number in 23..25 }
                19 -> allSurah.filter { x -> x.number in 25..27 }
                20 -> allSurah.filter { x -> x.number in 27..29 }
                21 -> allSurah.filter { x -> x.number in 29..33 }
                22 -> allSurah.filter { x -> x.number in 33..36 }
                23 -> allSurah.filter { x -> x.number in 36..38 }
                24 -> allSurah.filter { x -> x.number in 39..41 }
                25 -> allSurah.filter { x -> x.number in 41..45 }
                26 -> allSurah.filter { x -> x.number in 46..51 }
                27 -> allSurah.filter { x -> x.number in 51..57 }
                28 -> allSurah.filter { x -> x.number in 58..66 }
                29 -> allSurah.filter { x -> x.number in 67..77 }
                30 -> allSurah.filter { x -> x.number in 78..144 }
                else -> allSurah
            }
        } ?: return emptyList()
    }

    fun searchSurah(stringName: String): List<MsSurah> {
        allSurah.value?.data?.let{
            val lowerCaseString = if(stringName.isNotEmpty()) stringName.lowercase(Locale.ROOT).trim() else ""
            val list = it.filter { surah ->
                surah.englishNameLowerCase!!.contains(lowerCaseString)
            }
            return list
        } ?: run {
            return emptyList()
        }
    }

}