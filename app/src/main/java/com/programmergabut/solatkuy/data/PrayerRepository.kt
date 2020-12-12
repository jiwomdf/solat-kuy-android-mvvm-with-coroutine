package com.programmergabut.solatkuy.data

import androidx.lifecycle.LiveData
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsSetting
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.util.Resource

interface PrayerRepository {
    suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean)
    fun getMsApi1(): LiveData<Resource<MsApi1>>
    suspend fun updateMsApi1(msApi1: MsApi1)
    fun getMsSetting(): LiveData<Resource<MsSetting>>
    suspend fun updateIsUsingDBQuotes(isUsingDBQuotes: Boolean)
    suspend fun updateMsApi1MonthAndYear(api1ID: Int, month: String, year:String)
    suspend fun updateIsHasOpenApp(isHasOpen: Boolean)
    suspend fun fetchCompass(msApi1: MsApi1): CompassResponse
    suspend fun fetchPrayerApi(msApi1: MsApi1): PrayerResponse
    suspend fun syncNotifiedPrayer(msApi1: MsApi1): List<NotifiedPrayer>
    suspend fun syncNotifiedPrayerTesting(): List<NotifiedPrayer>

}