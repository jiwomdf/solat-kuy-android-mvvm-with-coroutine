package com.programmergabut.solatkuy.data.remote

import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.ContextProviders
import com.programmergabut.solatkuy.data.remote.api.AllSurahService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahArService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahEnService
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahApi
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArApi
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnApi
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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


    fun fetchReadSurahEn(surahID: String): MutableLiveData<Resource<ReadSurahEnApi>> {
        val result = MutableLiveData<Resource<ReadSurahEnApi>>()

        GlobalScope.launch(contextProviders.IO){
            try {
                result.postValue(
                    Resource.success(
                    RetrofitBuilder
                        .build<ReadSurahEnService>(strApi)
                        .fetchReadSurahEn(surahID))
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

    fun fetchReadSurahAr(surahID: String): MutableLiveData<Resource<ReadSurahArApi>> {
        val result = MutableLiveData<Resource<ReadSurahArApi>>()

        GlobalScope.launch(contextProviders.IO){
            try {

                val listAyah: MutableList<Ayah>

                val retValAr = withContext(Dispatchers.Default) {
                        RetrofitBuilder.build<ReadSurahArService>(strApi).fetchReadSurahAr(surahID)
                    }

                val retValEn = withContext(Dispatchers.Default) {
                        RetrofitBuilder.build<ReadSurahEnService>(strApi).fetchReadSurahEn(surahID)
                    }

                listAyah = retValAr.data.ayahs.map { x -> Ayah(
                    x.hizbQuarter,
                    x.juz,
                    x.manzil,
                    x.number,
                    x.numberInSurah,
                    x.page,
                    x.ruku,
                    x.sajda,
                    x.text,
                    "")} as MutableList<Ayah>

                retValEn.data.ayahs.forEachIndexed { index, x ->
                    listAyah[index].textEn = x.text
                }

                retValAr.data.ayahs = listAyah

                result.postValue(Resource.success(retValAr))
            }
            catch (ex: Exception){
                result.postValue(Resource.error(ex.message.toString(), null))
            }
        }

        return result
    }

}