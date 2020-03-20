package com.programmergabut.solatkuy.data.repository

import androidx.lifecycle.LiveData
import com.programmergabut.solatkuy.data.local.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.model.PrayerLocal

class RepositoryLocal(private val notifiedPrayerDao: NotifiedPrayerDao) {

    val prayerLocal: LiveData<List<PrayerLocal>> = notifiedPrayerDao.getNotifiedPrayer()

    suspend fun insert(prayerLocal: PrayerLocal){
        notifiedPrayerDao.insertNotifiedPrayer(prayerLocal)
    }

}