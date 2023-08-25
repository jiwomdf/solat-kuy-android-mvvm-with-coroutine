package com.programmergabut.solatkuy.data.repository

import androidx.lifecycle.LiveData
import com.programmergabut.solatkuy.data.remote.json.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.json.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.Deferred

interface PrayerRepository {
    suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean)
    suspend fun updatePrayerTime(prayerName: String, prayerTime: String)
    fun observeMsConfiguration(): LiveData<MsConfiguration>
    suspend fun updateMsConfiguration(msConfiguration: MsConfiguration)
    suspend fun updateMsConfigurationMethod(api1ID: Int, methodID: String)
    fun observeMsSetting(): LiveData<MsSetting>
    suspend fun updateMsConfigurationMonthAndYear(api1ID: Int, month: String, year:String)
    suspend fun updateIsHasOpenApp(isHasOpen: Boolean)
    suspend fun getListNotifiedPrayer(): List<MsNotifiedPrayer>
    suspend fun fetchQibla(msConfiguration: MsConfiguration): Deferred<Resource<CompassResponse>>
    suspend fun fetchPrayerApi(msConfiguration: MsConfiguration): Deferred<Resource<PrayerResponse>>
    fun getListNotifiedPrayer(msConfiguration: MsConfiguration): LiveData<Resource<List<MsNotifiedPrayer>>>
    fun getMethods(): LiveData<Resource<List<MsCalculationMethods>>>
}