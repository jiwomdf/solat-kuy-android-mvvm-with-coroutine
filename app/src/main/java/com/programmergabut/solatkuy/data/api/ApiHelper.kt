package com.programmergabut.solatkuy.data.api

import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import io.reactivex.Single

class ApiHelper(private val apiService: ApiService) {

    fun getPrayer() : Single<PrayerApi>{
      return apiService.getPrayer()
    }

}