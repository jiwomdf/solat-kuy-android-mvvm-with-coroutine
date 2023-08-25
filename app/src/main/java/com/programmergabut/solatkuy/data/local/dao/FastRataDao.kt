package com.programmergabut.solatkuy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.programmergabut.solatkuy.data.local.localentity.FastRataItemEntity

@Dao
interface FastRataDao {

    @Query("select * from fast_rata_items")
    fun getFastRataItems(): LiveData<List<FastRataItemEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFastRataItems(data: FastRataItemEntity)
}