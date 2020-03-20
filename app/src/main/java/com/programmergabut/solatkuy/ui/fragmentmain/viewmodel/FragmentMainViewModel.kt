package com.programmergabut.solatkuy.ui.fragmentmain.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.local.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.model.NotifiedPrayer
import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import com.programmergabut.solatkuy.data.repository.MainRepository
import com.programmergabut.solatkuy.data.repository.RepositoryLocal
import com.programmergabut.solatkuy.room.NotifiedPrayerRoom
import com.programmergabut.solatkuy.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class FragmentMainViewModel(private val mainRepository: MainRepository,application: Application): AndroidViewModel(application) {

    private val prayers = MutableLiveData<Resource<PrayerApi>>()
    private val compositeDisposable = CompositeDisposable()
    private var repository: RepositoryLocal? = null

    val notifiedPrayer: LiveData<List<NotifiedPrayer>>

    //Room
    init {
        val notifiedPrayerDao = NotifiedPrayerRoom.getDataBase(application, viewModelScope).notifiedPrayerDao()
        repository = RepositoryLocal(notifiedPrayerDao)

        notifiedPrayer = repository!!.notifiedPrayer
    }

    fun insert(notifiedPrayer: NotifiedPrayer) = viewModelScope.launch {
        repository?.insert(notifiedPrayer)
    }

    //Live Data
    fun fetchPrayer(){
        prayers.postValue(Resource.loading(null))
        compositeDisposable.addAll(
            mainRepository.getPrayer()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    prayers.postValue(Resource.success(it))},{
                    prayers.postValue(Resource.error(it.message.toString(), null))
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getPrayer(): LiveData<Resource<PrayerApi>> {
        return prayers
    }

}