package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.programmergabut.solatkuy.util.EnumStatus
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class NetworkBoundResource<ResultType, RequestType>

constructor(private val contextProviders: ContextProviders) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        setValue(Resource.loading(null))

        @Suppress("LeakingThis")
        val dbSource = loadFromDB()

        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data))
                fetchFromNetwork(dbSource)
            else {
                result.addSource(dbSource) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    protected fun onFetchFailed() {}

    protected abstract fun loadFromDB(): LiveData<ResultType> //mengakses data dari local database

    protected abstract fun shouldFetch(data: ResultType?): Boolean //apakah perlu akses remote database atau tidak

    protected abstract fun createCall(): LiveData<Resource<RequestType>> //untuk mengakses remote database

    protected abstract fun saveCallResult(data: RequestType) //untuk menyimpan data hasil dari remote database ke local database

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {

        val apiResponse = createCall()

        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(newData))
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response.Status) {
                EnumStatus.SUCCESS ->
                    GlobalScope.launch(contextProviders.IO) {
                        //EspressoIdlingResource.increment()
                        saveCallResult(response.data!!)

                        GlobalScope.launch(contextProviders.Main){
                            result.addSource(loadFromDB()) { newData ->
                                setValue(Resource.success(newData))
                            }
                        }
                        //EspressoIdlingResource.decrement()
                    }
                EnumStatus.LOADING -> GlobalScope.launch(contextProviders.Main) {
                    //EspressoIdlingResource.increment()
                    result.addSource(loadFromDB()) { newData ->
                        setValue(Resource.success(newData))
                    }
                    //EspressoIdlingResource.decrement()
                }
                EnumStatus.ERROR -> {
                    //EspressoIdlingResource.increment()
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.error(response.message!!, newData))
                    }
                    //EspressoIdlingResource.decrement()
                }
            }
        }
    }

    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    fun asLiveData(): LiveData<Resource<ResultType>> = result
}
