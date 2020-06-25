package com.programmergabut.solatkuy.data.remote

import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.remote.api.AllSurahService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahArService
import com.programmergabut.solatkuy.data.remote.api.ReadSurahEnService
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.AllSurahResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.EspressoIdlingResource
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.*

class RemoteDataSourceApiAlquran {

    private val strApi = "https://api.alquran.cloud/v1/"

    companion object{
        @Volatile
        private var instance: RemoteDataSourceApiAlquran? = null

        fun getInstance() = instance
            ?: synchronized(this){
                instance
                    ?: RemoteDataSourceApiAlquran()
            }
    }


    fun fetchReadSurahEn(surahID: Int): MutableLiveData<Resource<ReadSurahEnResponse>> {
        val result = MutableLiveData<Resource<ReadSurahEnResponse>>()
        result.postValue(Resource.loading(null))

        GlobalScope.launch(Dispatchers.IO){
            //EspressoIdlingResource.increment()
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
            //EspressoIdlingResource.decrement()
        }

        return result
    }


    fun fetchAllSurah(): MutableLiveData<Resource<AllSurahResponse>> {
        val result = MutableLiveData<Resource<AllSurahResponse>>()
        result.postValue(Resource.loading(null))

        GlobalScope.launch(Dispatchers.IO) {
            //EspressoIdlingResource.increment()
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
            //EspressoIdlingResource.decrement()
        }

        return result
    }

    fun fetchReadSurahAr(surahID: Int): MutableLiveData<Resource<ReadSurahArResponse>> {
        val result = MutableLiveData<Resource<ReadSurahArResponse>>()
        result.postValue(Resource.loading(null))

        GlobalScope.launch(Dispatchers.IO) {
            //EspressoIdlingResource.increment()
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
                    "",
                    false)} as MutableList<Ayah>

                retValEn.data.ayahs.forEachIndexed { index, x ->
                    listAyah[index].textEn = x.text
                }


                retValAr.data.ayahs = listAyah

                result.postValue(Resource.success(retValAr))
            }
            catch (ex: Exception){
                result.postValue(Resource.error(ex.message.toString(), null))
            }
            //EspressoIdlingResource.decrement()
        }

        return result
    }

}