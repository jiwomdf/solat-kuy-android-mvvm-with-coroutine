package com.programmergabut.solatkuy.ui.main.quran.listsurah

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsSurah
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.SharedPrefUtil
import com.programmergabut.solatkuy.util.livedata.AbsentLiveData


class ListSurahViewModel
    (
    private val quranRepository: QuranRepository,
    val sharedPrefUtil: SharedPrefUtil
) :
    ViewModel() {

    val staredSurah = quranRepository.observeListFavSurah()

    private var isAyahsCalled = MutableLiveData(false)
    val allSurah: LiveData<Resource<List<MsSurah>>> =
        Transformations.switchMap(isAyahsCalled) { isAyahsCalled ->
            if (!isAyahsCalled) {
                AbsentLiveData.create()
            } else {
                quranRepository.getAllSurah()
            }
        }

    fun getAllSurah(value: Boolean = true) {
        this.isAyahsCalled.value = value
    }

}