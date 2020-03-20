package com.programmergabut.solatkuy.data.repository

import androidx.lifecycle.LiveData
import com.programmergabut.solatkuy.data.local.NotifiedPrayerDao
import com.programmergabut.solatkuy.data.model.NotifiedPrayer

class RepositoryLocal(private val notifiedPrayerDao: NotifiedPrayerDao) {

    val notifiedPrayer: LiveData<List<NotifiedPrayer>> = notifiedPrayerDao.getNotifiedPrayer()

    suspend fun insert(notifiedPrayer: NotifiedPrayer){
        notifiedPrayerDao.insertNotifiedPrayer(notifiedPrayer)
    }

}