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
    fun insertAyahs(msAyah: List<MsAyah>)

    @Query("DELETE FROM ms_ayah WHERE surahID = :surahID")
    fun deleteAyahsBySurahID(surahID: Int)

    @Query("SELECT * FROM ms_ayah WHERE surahID = :surahID")
    fun getAyahsBySurahID(surahID: Int): LiveData<List<MsAyah>>
}