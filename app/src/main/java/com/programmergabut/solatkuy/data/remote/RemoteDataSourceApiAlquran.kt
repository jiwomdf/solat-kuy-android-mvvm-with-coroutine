package com.programmergabut.solatkuy.data.remote

import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.ContextProviders
import com.programmergabut.solatkuy.data.remote.api.AllSurahService
import com.programmergabut.solatkuy.data.remote.api.QuranSurahService
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahApi
import com.programmergabut.solatkuy.data.remote.remoteentity.quransurahJson.QuranSurahApi
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RemoteDataSourceApiAlquran(private val contextProviders: ContextProviders) {

    private val strApi = "https://api.alquran.cloud/v1/"

    companion object{
        @Volatile
        private var instance: RemoteDataSourceApiAlquran? = null

        fun getInstance(contextProviders: ContextProviders) = instance
            ?: synchronized(this){
                instance
                    ?: RemoteDataSourceApiAlquran(contextProviders)
            }
    }


    fun fetchQuranSurah(nInSurah: String): MutableLiveData<Resource<QuranSurahApi>> {
        val result = MutableLiveData<Resource<QuranSurahApi>>()

        GlobalScope.launch(contextProviders.IO){
            try {
                result.postValue(
                    Resource.success(
                    RetrofitBuilder
                        .build<QuranSurahService>(strApi)
                        .fetchQuranSurah(nInSurah))
                )
            }
            catch (ex: Exception){
                result.postValue(Resource.error(ex.message.toString(), null))
            }
        }

        return result
    }


    fun fetchAllSurah(): MutableLiveData<Resource<AllSurahApi>> {
        val result = MutableLiveData<Resource<AllSurahApi>>()

        GlobalScope.launch(contextProviders.IO){
            try {
                result.postValue(
                    Resource.success(
                        RetrofitBuilder
                            .build<AllSurahService>(strApi)
                            .fetchAllSurah())
                )
            }
            catch (ex: Exception){
                result.postValue(Resource.error(ex.message.toString(), null))
            }
        }

        return result
    }

}