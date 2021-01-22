package com.programmergabut.solatkuy.data.remote

import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import retrofit2.Call

interface RemoteDataSourceAladhan {
    fun fetchCompassApi(msApi1: MsApi1): CompassResponse
    fun fetchPrayerApi(msApi1: MsApi1):  PrayerResponse
}