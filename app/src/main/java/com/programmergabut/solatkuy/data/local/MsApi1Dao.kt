package com.programmergabut.solatkuy.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.programmergabut.solatkuy.data.model.dao.MsApi1

@Dao
interface MsApi1Dao {

    @Query("select * from MsApi1")
    fun getMsApi1(): LiveData<MsApi1>

    @Query("delete from MsApi1")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMsApi1(msApi1: MsApi1)

    @Query("update MsApi1 set latitude = :latitude, longitude = :longitude, method = :method, month = :month, year = :year where api1ID = :api1ID")
    suspend fun updateMsApi1(api1ID: Int, latitude: String, longitude: String, method: String, month: String, year:String)

}