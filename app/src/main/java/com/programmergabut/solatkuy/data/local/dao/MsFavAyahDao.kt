package com.programmergabut.solatkuy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import kotlinx.coroutines.Deferred

@Dao
interface MsFavAyahDao {

    @Query("select * from MsFavAyah")
    fun getListFavAyah(): LiveData<List<MsFavAyah>>

    @Query("select * from MsFavAyah where surahID like :surahID")
    suspend fun getListFavAyahBySurahID(surahID: Int): List<MsFavAyah>

    @Query("select * from MsFavAyah where ayahID like :ayahID and surahID like :surahID")
    fun isFavAyah(ayahID: Int, surahID: Int): MsFavAyah

    @Query("delete from MsFavAyah")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteMsFavAyah(msFavAyah: MsFavAyah)

    @Query("delete from MsFavAyah where ayahID like :ayahID and surahID like :surahID")
    suspend fun deleteMsFavAyahByID(ayahID: Int, surahID: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMsAyah(msFavAyah: MsFavAyah)

}