package com.programmergabut.solatkuy.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.programmergabut.solatkuy.data.model.NotifiedPrayer

@Dao
interface NotifiedPrayerDao {

    @Query("select * from notified_prayer order by prayerID asc")
    fun getNotifiedPrayer(): LiveData<List<NotifiedPrayer>>

    @Query("delete from notified_prayer")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotifiedPrayer(notifiedPrayer: NotifiedPrayer)

}