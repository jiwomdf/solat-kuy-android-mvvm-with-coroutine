package com.programmergabut.solatkuy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah

@Dao
interface MsFavAyahDao {

    @Query("select * from MsFavAyah where ayahID like :ayahID")
    fun getMsFavAyahByID(ayahID: Int): LiveData<List<MsFavAyah>>

    @Query("select * from MsFavAyah where ayahID like :ayahID and surahID like :surahID")
    fun isFavAyah(ayahID: Int, surahID: Int): LiveData<MsFavAyah?>

    @Query("delete from MsFavAyah")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMsApi1(msFavAyah: MsFavAyah)

}