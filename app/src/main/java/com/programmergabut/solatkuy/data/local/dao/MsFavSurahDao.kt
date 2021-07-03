package com.programmergabut.solatkuy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah

@Dao
interface MsFavSurahDao {

    @Query("select * from ms_fav_surah")
    fun observeFavSurahs(): LiveData<List<MsFavSurah>>

    @Query("select * from ms_fav_surah where surahID like :surahID")
    fun observeFavSurahBySurahID(surahID: Int): LiveData<MsFavSurah?>

    @Query("delete from ms_fav_surah")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteMsFavSurah(msFavSurah: MsFavSurah)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMsSurah(msFavSurah: MsFavSurah)

}