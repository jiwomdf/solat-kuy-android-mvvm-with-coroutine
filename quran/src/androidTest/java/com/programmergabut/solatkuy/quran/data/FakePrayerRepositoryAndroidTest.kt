package com.programmergabut.solatkuy.quran.data

/*
 * Created by Katili Jiwo Adi Wiyono on 26/03/20.
 */

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.programmergabut.solatkuy.data.local.localentity.MsCalculationMethods
import com.programmergabut.solatkuy.data.local.localentity.MsConfiguration
import com.programmergabut.solatkuy.data.local.localentity.MsNotifiedPrayer
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.data.remote.json.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.json.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.repository.PrayerRepository
import com.programmergabut.solatkuy.quran.DummyValueAndroidTest
import com.programmergabut.solatkuy.util.Resource
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async

@HiltAndroidTest
class FakePrayerRepositoryAndroidTest : PrayerRepository {

    private var msConfiguration = DummyValueAndroidTest.getMsConfiguration()
    private var msSetting = DummyValueAndroidTest.getMsSetting()
    private var notifiedPrayer = DummyValueAndroidTest.getNotifiedPrayer()
    private var msCalculationMethods = DummyValueAndroidTest.getMsCalculationMethods()

    private val observableMsConfiguration = MutableLiveData<MsConfiguration>()
    private val observableMsSetting = MutableLiveData<MsSetting>()
    private val observableNotifiedPrayer = MutableLiveData<List<MsNotifiedPrayer>>()

    init {
        observableMsConfiguration.postValue(msConfiguration)
        observableMsSetting.postValue(msSetting)
        observableNotifiedPrayer.postValue(notifiedPrayer)
    }

    private fun refreshMsConfiguration(){
        observableMsConfiguration.postValue(msConfiguration)
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

    /* MsConfiguration */
    override fun observeMsConfiguration(): LiveData<MsConfiguration> {
        return observableMsConfiguration
    }
    override suspend fun updateMsConfiguration(msConfiguration: MsConfiguration){
        this.msConfiguration = msConfiguration
        refreshMsConfiguration()
    }

    override suspend fun updateMsConfigurationMethod(api1ID: Int, methodID: String) {
        msConfiguration = MsConfiguration(api1ID, msConfiguration.latitude, msConfiguration.longitude, methodID, msConfiguration.month, msConfiguration.year)
    }

    override fun observeMsSetting(): LiveData<MsSetting> {
        return observableMsSetting
    }

    /* MsSetting */
    override suspend fun updateMsConfigurationMonthAndYear(api1ID: Int, month: String, year:String){
        msConfiguration = MsConfiguration(api1ID, msConfiguration.latitude, msConfiguration.longitude, msConfiguration.method, month, year)
        refreshMsSetting()
    }
    override suspend fun updateIsHasOpenApp(isHasOpen: Boolean){
        msSetting = MsSetting(msSetting.no, isHasOpen)
        refreshMsSetting()
    }

    /* Retrofit */
    override suspend fun fetchQibla(msConfiguration: MsConfiguration): Deferred<Resource<CompassResponse>> {
        return CoroutineScope(IO).async {
            val data = DummyValueAndroidTest.fetchCompassApi<FakePrayerRepositoryAndroidTest>()
            data.message = "testing"
            Resource.success(data)
        }
    }
    override suspend fun fetchPrayerApi(msConfiguration: MsConfiguration): Deferred<Resource<PrayerResponse>> {
        return CoroutineScope(IO).async {
            val data = DummyValueAndroidTest.fetchPrayerApi<FakePrayerRepositoryAndroidTest>()
            data.message = "testing"
            Resource.success(data)
        }
    }

    override fun getMethods(): LiveData<Resource<List<MsCalculationMethods>>> {
        return liveData {
            emit(Resource.success(msCalculationMethods))
        }
    }

    override fun getListNotifiedPrayer(msConfiguration: MsConfiguration): LiveData<Resource<List<MsNotifiedPrayer>>> {
        return liveData {
            emit(Resource.success(notifiedPrayer))
        }
    }

    override suspend fun getListNotifiedPrayer(): List<MsNotifiedPrayer> {
        return observableNotifiedPrayer.value!!
    }
}
