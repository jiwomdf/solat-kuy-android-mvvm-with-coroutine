package com.programmergabut.solatkuy.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.programmergabut.solatkuy.data.model.entity.PrayerLocal

@Dao
interface NotifiedPrayerDao {

    @Query("select * from notified_prayer order by prayerID asc")
    fun getNotifiedPrayer(): LiveData<List<PrayerLocal>>

    @Query("delete from notified_prayer")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotifiedPrayer(prayerLocal: PrayerLocal)

    @Query("update notified_prayer set isNotified = :isNotified and prayerTime = :prayerTime where prayerName = :prayerName")
    suspend fun updateNotifiedPrayer(prayerName: String, isNotified: Boolean, prayerTime: String)

    @Query("update notified_prayer set prayerTime = :prayerTime where prayerName = :prayerName")
    suspend fun updatePrayerTime(prayerName: String, prayerTime: String)

    @Query("update notified_prayer set isNotified = :isNotified where prayerName = :prayerName")
    suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean)
}