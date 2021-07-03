package com.programmergabut.solatkuy.ui.main.quran.listsurah

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.QuranRepository
import com.programmergabut.solatkuy.data.local.localentity.MsSurah
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.livedata.AbsentLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
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

}