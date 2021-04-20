package com.programmergabut.solatkuy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer

/*
 * Created by Katili Jiwo Adi Wiyono on 27/03/20.
 */


@Dao
interface NotifiedPrayerDao {
    @Query("select * from notified_prayer order by prayerID asc")
    fun observeListNotifiedPrayer(): LiveData<List<NotifiedPrayer>>

    @Query("select * from notified_prayer order by prayerID asc")
    fun getListNotifiedPrayerSync(): List<NotifiedPrayer>

    @Query("select * from notified_prayer order by prayerID asc")
    fun getListNotifiedPrayer(): LiveData<List<NotifiedPrayer>>

    @Query("delete from notified_prayer")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotifiedPrayer(NotifiedPrayer: NotifiedPrayer)

    @Query("update notified_prayer set isNotified = :isNotified and prayerTime = :prayerTime where prayerName = :prayerName")
    suspend fun updateNotifiedPrayer(prayerName: String, isNotified: Boolean, prayerTime: String)

    @Query("update notified_prayer set prayerTime = :prayerTime where prayerName = :prayerName")
    fun updatePrayerTime(prayerName: String, prayerTime: String)

    @Query("update notified_prayer set isNotified = :isNotified where prayerName = :prayerName")
    suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean)
}