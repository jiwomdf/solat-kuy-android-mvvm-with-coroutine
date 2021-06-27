package com.programmergabut.solatkuy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah

@Dao
interface MsFavAyahDao {

    @Query("select * from ms_fav_ayah")
    fun observeListFavAyah(): LiveData<List<MsFavAyah>>

    @Query("select * from ms_fav_ayah where surahID like :surahID")
    fun getListFavAyahBySurahID(surahID: Int): List<MsFavAyah>?

    @Query("delete from ms_fav_ayah")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteMsFavAyah(msFavAyah: MsFavAyah)

    @Query("delete from ms_fav_ayah where ayahID like :ayahID and surahID like :surahID")
    suspend fun deleteMsFavAyahByID(ayahID: Int, surahID: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMsAyah(msFavAyah: MsFavAyah)

}