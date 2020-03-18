package com.programmergabut.solatkuy.data.api

import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Single

class ApiServiceImpl: ApiService {

    override fun getPrayer(): Single<PrayerApi> {

        return Rx2AndroidNetworking
            .get("http://api.aladhan.com/v1/calendar?latitude=-7.55611&longitude=110.83167&method=2&month=3&year=2020")
            .build()
            .getObjectSingle(PrayerApi::class.java)
    }
}