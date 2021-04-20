package com.programmergabut.solatkuy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.programmergabut.solatkuy.data.local.localentity.MsSurah

@Dao
interface MsSurahDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSurahs(msSurah: List<MsSurah>)

    @Query("delete from MsSurah")
    fun deleteSurahs()

    @Query("select * from MsSurah")
    fun getSurahs(): LiveData<List<MsSurah>>
}