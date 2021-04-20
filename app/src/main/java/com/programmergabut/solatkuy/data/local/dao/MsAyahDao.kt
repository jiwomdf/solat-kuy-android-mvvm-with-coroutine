package com.programmergabut.solatkuy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.programmergabut.solatkuy.data.local.localentity.MsAyah
import com.programmergabut.solatkuy.data.local.localentity.MsSurah

@Dao
interface MsAyahDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAyahs(msSurah: List<MsAyah>)

    @Query("delete from MsAyah")
    fun deleteAyahs()

    @Query("select * from MsAyah")
    fun getAyahs(): LiveData<List<MsAyah>>
}