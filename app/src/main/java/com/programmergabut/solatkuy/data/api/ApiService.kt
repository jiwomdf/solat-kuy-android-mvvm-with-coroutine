package com.programmergabut.solatkuy.data.api

import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import io.reactivex.Single

interface ApiService {

    fun getPrayer(): Single<PrayerApi>

}