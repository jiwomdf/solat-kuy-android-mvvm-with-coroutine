package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.DummyRetValueAndroidTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

class FakePrayerRepositoryAndroidTest: PrayerRepository {

    private var msApi11 = DummyRetValueAndroidTest.getMsApi1()
    private val observableMsApi1 = MutableLiveData<MsApi1>()
    private var msSetting = DummyRetValueAndroidTest.getMsSetting()
    private val observableMsSetting = MutableLiveData<MsSetting>()
    private var notifiedPrayer = DummyRetValueAndroidTest.getNotifiedPrayer()
    private val observableNotifiedPrayer = MutableLiveData<List<NotifiedPrayer>>()

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
    override fun updatePrayerTime(prayerName: String, prayerTime: String) {
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
    override suspend fun fetchCompass(msApi1: MsApi1): Deferred<CompassResponse> {
        return CoroutineScope(IO).async {
            val data = DummyRetValueAndroidTest.fetchCompassApi<FakePrayerRepositoryAndroidTest>()
            data.statusResponse = "1"
            data.messageResponse = "testing"
            data
        }
    }
    override suspend fun fetchPrayerApi(msApi1: MsApi1): Deferred<PrayerResponse> {
        return CoroutineScope(IO).async {
            val data = DummyRetValueAndroidTest.fetchPrayerApi<FakePrayerRepositoryAndroidTest>()
            data.statusResponse = "1"
            data.messageResponse = "testing"
            data
        }
    }
    override suspend fun syncNotifiedPrayerTesting(): List<NotifiedPrayer> {
        return DummyRetValueAndroidTest.getNotifiedPrayer()
    }
    override suspend fun getListNotifiedPrayer(): List<NotifiedPrayer>? {
        return observableNotifiedPrayer.value!!
    }
}

