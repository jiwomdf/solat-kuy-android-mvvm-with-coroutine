package com.programmergabut.solatkuy.ui.prayerdetail.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import com.programmergabut.solatkuy.data.repository.MainRepository
import com.programmergabut.solatkuy.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ActivityPrayerViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val prayers = MutableLiveData<Resource<PrayerApi>>()
    private val compositeDisposable = CompositeDisposable()

    fun fetchPrayer(){
        prayers.postValue(Resource.loading(null))
        compositeDisposable.add(
            mainRepository.getPrayer()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                prayers.postValue(Resource.success(it))
            },{
                prayers.postValue(Resource.error(it.message.toString(),null))
                Log.d("Main_view_model",it.message.toString())
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getPrayer():LiveData<Resource<PrayerApi>>{
        return prayers
    }
}