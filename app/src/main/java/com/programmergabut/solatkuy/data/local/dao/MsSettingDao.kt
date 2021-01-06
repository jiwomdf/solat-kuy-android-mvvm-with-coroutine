package com.programmergabut.solatkuy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.programmergabut.solatkuy.data.local.localentity.MsSetting

@Dao
interface MsSettingDao {

    @Query("select * from MsSetting")
    fun observeMsSetting(): LiveData<MsSetting>

    @Query("select * from MsSetting")
    suspend fun getMsSetting(): MsSetting

    @Query("delete from MsSetting")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMsSetting(msSetting: MsSetting)

    @Query("update MsSetting set isHasOpenApp = :isHasOpen where `no` = 1")
    suspend fun updateIsHasOpenApp(isHasOpen: Boolean)

    @Query("update MsSetting set isUsingDBQuotes = :isUsingDBQuotes where `no` = 1")
    suspend fun updateIsUsingDBQuotes(isUsingDBQuotes: Boolean)

}