package com.programmergabut.solatkuy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.programmergabut.solatkuy.data.local.localentity.MsNotifiedPrayer

/*
 * Created by Katili Jiwo Adi Wiyono on 27/03/20.
 */


@Dao
interface MsNotifiedPrayerDao {
    @Query("select * from ms_notified_prayer order by prayerID asc")
    fun observeListNotifiedPrayer(): LiveData<List<MsNotifiedPrayer>>

    @Query("select * from ms_notified_prayer order by prayerID asc")
    fun getListNotifiedPrayerSync(): List<MsNotifiedPrayer>

    @Query("select * from ms_notified_prayer order by prayerID asc")
    fun getListNotifiedPrayer(): LiveData<List<MsNotifiedPrayer>>

    @Query("delete from ms_notified_prayer")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotifiedPrayer(MsNotifiedPrayer: MsNotifiedPrayer)

    @Query("update ms_notified_prayer set isNotified = :isNotified and prayerTime = :prayerTime where prayerName = :prayerName")
    suspend fun updateNotifiedPrayer(prayerName: String, isNotified: Boolean, prayerTime: String)

    @Query("update ms_notified_prayer set prayerTime = :prayerTime where prayerName = :prayerName")
    fun updatePrayerTime(prayerName: String, prayerTime: String)

    @Query("update ms_notified_prayer set isNotified = :isNotified where prayerName = :prayerName")
    suspend fun updatePrayerIsNotified(prayerName: String, isNotified: Boolean)
}