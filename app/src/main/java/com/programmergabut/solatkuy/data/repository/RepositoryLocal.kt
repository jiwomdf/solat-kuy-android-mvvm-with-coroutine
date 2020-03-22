package com.programmergabut.solatkuy.data.repository

import androidx.lifecycle.LiveData
import com.programmergabut.solatkuy.data.api.ApiHelper
import com.programmergabut.solatkuy.data.local.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.model.PrayerLocal
import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import io.reactivex.Single

class RepositoryLocal(private val notifiedPrayerDao: NotifiedPrayerDao, private val apiHelper: ApiHelper) {

    val prayerLocal: LiveData<List<PrayerLocal>> = notifiedPrayerDao.getNotifiedPrayer()

    suspend fun update(prayerLocal: PrayerLocal){
        notifiedPrayerDao.updateNotifiedPrayer(prayerLocal.prayerName, prayerLocal.isNotified)
    }

    fun getPrayer(): Single<PrayerApi> {
        return apiHelper.getPrayer()
    }

    fun getPrayer(latitude:String, longitude: String): Single<PrayerApi> {
        return apiHelper.getPrayer(latitude,longitude)
    }

}