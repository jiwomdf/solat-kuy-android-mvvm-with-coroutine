package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.programmergabut.solatkuy.data.remote.ApiResponse
import com.programmergabut.solatkuy.data.remote.StatusResponse
import com.programmergabut.solatkuy.util.ContextProviders
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class NetworkBoundResource<ResultType, RequestType>(
    private val contextProviders: ContextProviders
) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)

        @Suppress("LeakingThis")
        val dbSource = loadFromDB()

        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data))
                fetchFromNetwork(dbSource)
            else {
                result.addSource(dbSource) { newData ->
                    result.value = Resource.success(newData)
                }
            }
        }
    }

    protected abstract fun onFetchFailed()

    protected abstract fun loadFromDB(): LiveData<ResultType> //mengakses data dari local database

    protected abstract fun shouldFetch(data: ResultType?): Boolean //apakah perlu akses remote database atau tidak

    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>> //untuk mengakses remote database

    protected abstract fun saveCallResult(data: RequestType) //untuk menyimpan data hasil dari remote database ke local database

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {

        val apiResponse = createCall()

        result.addSource(dbSource) { newData ->
            result.value = Resource.loading(newData)
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response.status) {
                StatusResponse.SUCCESS ->
                    CoroutineScope(contextProviders.IO).launch {
                        saveCallResult(response.body)
                        withContext(Dispatchers.Main) {
                            result.addSource(loadFromDB()) { newData ->
                                result.value = Resource.success(newData)
                            }
                        }
                    }
                StatusResponse.EMPTY -> CoroutineScope(contextProviders.Main).launch {
                    result.addSource(loadFromDB()) { newData ->
                        result.value = Resource.success(newData)
                    }
                }
                StatusResponse.ERROR -> {
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        result.value = Resource.error(newData, response.message!!)
                    }
                }
            }
        }
    }

    fun asLiveData(): LiveData<Resource<ResultType>> = result
}
