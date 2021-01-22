package com.programmergabut.solatkuy.data.remote

import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.util.DebugUtil.Companion.execute
import com.programmergabut.solatkuy.data.remote.api.CalendarApiService
import com.programmergabut.solatkuy.data.remote.api.QiblaApiService
import com.programmergabut.solatkuy.data.remote.remoteentity.compassJson.CompassResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import javax.inject.Inject

/*
 * Created by Katili Jiwo Adi Wiyono on 09/05/20.
 */

class RemoteDataSourceAladhanImpl @Inject constructor(
    private val qiblaApiService: QiblaApiService,
    private val calendarApiService: CalendarApiService
) : RemoteDataSourceAladhan{

    override fun fetchCompassApi(msApi1: MsApi1): CompassResponse {
        return execute(qiblaApiService.fetchQibla(msApi1.latitude, msApi1.longitude))
    }

    override fun fetchPrayerApi(msApi1: MsApi1): PrayerResponse {
        return execute(calendarApiService.fetchPrayer(msApi1.latitude, msApi1.longitude, msApi1.method, msApi1.month, msApi1.year))
    }

}