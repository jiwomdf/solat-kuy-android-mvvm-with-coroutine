package com.programmergabut.solatkuy.ui.fastrata

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.programmergabut.solatkuy.data.local.localentity.FastRataItemEntity
import com.programmergabut.solatkuy.data.repository.QuranRepository
import com.programmergabut.solatkuy.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FastRataViewModel @Inject constructor(
    private val repository: QuranRepository
): ViewModel() {

    private var _queries = MutableLiveData<Resource<List<String>>>()
    val queries: LiveData<Resource<List<String>>> = _queries


    fun getData(query: String) {
        try {
            val data = repository.getFastRata()
            val isEmpty = data.value.isNullOrEmpty()

            if(isEmpty) {
                repository.insertFastRataItems(FastRataItemEntity(name = query))
                getData(query)
            } else {
                val filteredData = data.value?.filter {
                    it.name.contains(query)
                }?.toMutableList()

                val newList = mutableListOf<String>()
                val size = filteredData?.size ?: 0
                for(i in 0 until size) {
                    val fixName = filteredData?.get(i)?.name ?: ""
                    val splitArr = fixName.split(" ")
                    newList.addAll(splitArr)
                }

                //TODO JIWO
                Log.e("jiwo", "getData: ${newList}", )

                _queries.postValue(Resource.success(newList))
            }
        } catch (ex: Exception) {
            _queries.postValue(Resource.error(null, msg = ex.message.toString()))
        }

    }
}