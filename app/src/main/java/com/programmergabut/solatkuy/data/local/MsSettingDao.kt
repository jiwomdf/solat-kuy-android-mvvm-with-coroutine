package com.programmergabut.solatkuy.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.programmergabut.solatkuy.data.model.entity.MsSetting

@Dao
interface MsSettingDao {

    @Query("select * from MsSetting")
    fun getMsSetting(): LiveData<MsSetting>

    @Query("delete from MsSetting")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMsSetting(msSetting: MsSetting)

    @Query("update MsSetting set isHasOpenApp = :isHasOpen where `no` = 1")
    suspend fun updateMsSetting(isHasOpen: Boolean)

}