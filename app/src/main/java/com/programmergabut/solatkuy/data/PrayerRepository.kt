package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsCalculationMethods
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.data.local.localentity.MsNotifiedPrayer
import com.programmergabut.solatkuy.data.remote.json.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.json.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.util.Resource
import kotlinx.coroutines.Deferred

interface PrayerRepository {
    suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean)
    suspend fun updatePrayerTime(prayerName: String, prayerTime: String)
    fun observeMsApi1(): LiveData<MsApi1>
    suspend fun updateMsApi1(msApi1: MsApi1)
    suspend fun updateMsApi1Method(api1ID: Int, methodID: String)
    fun observeMsSetting(): LiveData<MsSetting>
    suspend fun updateMsApi1MonthAndYear(api1ID: Int, month: String, year:String)
    suspend fun updateIsHasOpenApp(isHasOpen: Boolean)
    suspend fun getListNotifiedPrayer(): List<MsNotifiedPrayer>
    suspend fun fetchQibla(msApi1: MsApi1): Deferred<Resource<CompassResponse>>
    suspend fun fetchPrayerApi(msApi1: MsApi1): Deferred<Resource<PrayerResponse>>
    fun getListNotifiedPrayer(msApi1: MsApi1): LiveData<Resource<List<MsNotifiedPrayer>>>
    fun getMethods(): LiveData<Resource<List<MsCalculationMethods>>>
}