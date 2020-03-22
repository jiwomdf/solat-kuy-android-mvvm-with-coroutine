package com.programmergabut.solatkuy.ui.fragmentmain.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.programmergabut.solatkuy.data.model.MsApi1
import com.programmergabut.solatkuy.data.model.PrayerLocal
import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import com.programmergabut.solatkuy.data.repository.MainRepository
import com.programmergabut.solatkuy.data.repository.RepositoryLocal
import com.programmergabut.solatkuy.room.MsApi1Room
import com.programmergabut.solatkuy.room.NotifiedPrayerRoom
import com.programmergabut.solatkuy.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class FragmentMainViewModel(private val repositoryLocal: RepositoryLocal, application: Application): AndroidViewModel(application) {

    private val prayers = MutableLiveData<Resource<PrayerApi>>()
    private val compositeDisposable = CompositeDisposable()
    private var repository: RepositoryLocal? = null

    val prayerLocal: LiveData<List<PrayerLocal>>
    //val msApi1: LiveData<List<MsApi1>>

    //Room
    init {
        val notifiedPrayerDao = NotifiedPrayerRoom.getDataBase(application, viewModelScope).notifiedPrayerDao()
        repository = RepositoryLocal(notifiedPrayerDao)

//        val msApi1Dao = MsApi1Room.getDataBase(application, viewModelScope).msApi1Dao()
//        repository = RepositoryLocal(msApi1Dao)


        prayerLocal = repository!!.prayerLocal
    }

    fun update(prayerLocal: PrayerLocal) = viewModelScope.launch {
        repository?.update(prayerLocal)
    }

    //Live Data
    fun fetchPrayer(){
        prayers.postValue(Resource.loading(null))
        compositeDisposable.addAll(
            repositoryLocal.getPrayer()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    prayers.postValue(Resource.success(it))},{
                    prayers.postValue(Resource.error(it.message.toString(), null))
                })
        )
    }

    fun fetchPrayer(latitude:String, longitude: String){
        prayers.postValue(Resource.loading(null))
        compositeDisposable.addAll(
            repositoryLocal.getPrayer(latitude,longitude)
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