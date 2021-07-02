package com.programmergabut.solatkuy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.programmergabut.solatkuy.data.local.localentity.MsSetting

@Dao
interface MsSettingDao {

    @Query("select * from ms_setting")
    fun observeMsSetting(): LiveData<MsSetting>

    @Query("delete from ms_setting")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMsSetting(msSetting: MsSetting)

    @Query("update ms_setting set isHasOpenApp = :isHasOpen where `no` = 1")
    suspend fun updateIsHasOpenApp(isHasOpen: Boolean)

}