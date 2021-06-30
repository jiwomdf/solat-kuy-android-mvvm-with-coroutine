package com.programmergabut.solatkuy.data

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.programmergabut.solatkuy.DummyValueAndroidTest
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsCalculationMethods
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.data.local.localentity.MsNotifiedPrayer
import com.programmergabut.solatkuy.data.remote.json.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.json.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.util.Resource
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async

@HiltAndroidTest
class FakePrayerRepositoryAndroidTest : PrayerRepository {

    private var msApi11 = DummyValueAndroidTest.getMsApi1()
    private var msSetting = DummyValueAndroidTest.getMsSetting()
    private var notifiedPrayer = DummyValueAndroidTest.getNotifiedPrayer()
    private var msCalculationMethods = DummyValueAndroidTest.getMsCalculationMethods()

    private val observableMsApi1 = MutableLiveData<MsApi1>()
    private val observableMsSetting = MutableLiveData<MsSetting>()
    private val observableNotifiedPrayer = MutableLiveData<List<MsNotifiedPrayer>>()

    init {
        observableMsApi1.postValue(msApi11)
        observableMsSetting.postValue(msSetting)
        observableNotifiedPrayer.postValue(notifiedPrayer)
    }

    private fun refreshMsApi1(){
        observableMsApi1.postValue(msApi11)
    }
    private fun refreshMsSetting(){
        observableMsSetting.postValue(msSetting)
    }

    /* Room */
    /* NotifiedPrayer */
    override suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean){
        val newList = notifiedPrayer
        newList.map { data ->
            if(data.prayerName == prayerName){
                data.prayerName = prayerName
                data.isNotified = isNotified
            }
        }

        observableNotifiedPrayer.postValue(newList)
    }
    override suspend fun updatePrayerTime(prayerName: String, prayerTime: String) {
        val newList = notifiedPrayer
        newList.map { data ->
            if(data.prayerName == prayerName){
                data.prayerName = prayerName
                data.prayerTime = prayerTime
            }
        }

        observableNotifiedPrayer.postValue(newList)
    }

    /* MsApi1 */
    override fun observeMsApi1(): LiveData<MsApi1> {
        return observableMsApi1
    }
    override suspend fun updateMsApi1(msApi1: MsApi1){
        msApi11 = msApi1
        refreshMsApi1()
    }

    override suspend fun updateMsApi1Method(api1ID: Int, methodID: String) {
        msApi11 = MsApi1(api1ID, msApi11.latitude, msApi11.longitude, methodID, msApi11.month, msApi11.year)
    }

    override fun observeMsSetting(): LiveData<MsSetting> {
        return observableMsSetting
    }

    /* MsSetting */
    override suspend fun updateIsUsingDBQuotes(isUsingDBQuotes: Boolean){
        msSetting = MsSetting(msSetting.no, msSetting.isHasOpenApp, isUsingDBQuotes)
        refreshMsSetting()
    }
    override suspend fun updateMsApi1MonthAndYear(api1ID: Int, month: String, year:String){
        msApi11 = MsApi1(api1ID, msApi11.latitude, msApi11.longitude, msApi11.method, month, year)
        refreshMsSetting()
    }
    override suspend fun updateIsHasOpenApp(isHasOpen: Boolean){
        msSetting = MsSetting(msSetting.no, isHasOpen, msSetting.isUsingDBQuotes)
        refreshMsSetting()
    }

    /* Retrofit */
    override suspend fun fetchQibla(msApi1: MsApi1): Deferred<CompassResponse> {
        return CoroutineScope(IO).async {
            val data = DummyValueAndroidTest.fetchCompassApi<FakePrayerRepositoryAndroidTest>()
            data.responseStatus = "1"
            data.message = "testing"
            data
        }
    }
    override suspend fun fetchPrayerApi(msApi1: MsApi1): Deferred<PrayerResponse> {
        return CoroutineScope(IO).async {
            val data = DummyValueAndroidTest.fetchPrayerApi<FakePrayerRepositoryAndroidTest>()
            data.responseStatus = "1"
            data.message = "testing"
            data
        }
    }

    override fun getMethods(): LiveData<Resource<List<MsCalculationMethods>>> {
        return liveData {
            emit(Resource.success(msCalculationMethods))
        }
    }

    override fun getListNotifiedPrayer(msApi1: MsApi1): LiveData<Resource<List<MsNotifiedPrayer>>> {
        return liveData {
            emit(Resource.success(notifiedPrayer))
        }
    }

    override suspend fun getListNotifiedPrayer(): List<MsNotifiedPrayer> {
        return observableNotifiedPrayer.value!!
    }
}
