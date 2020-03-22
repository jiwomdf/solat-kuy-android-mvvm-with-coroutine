package com.programmergabut.solatkuy.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.programmergabut.solatkuy.data.model.MsApi1

@Dao
interface MsApi1Dao {

    @Query("delete from MsApi1")
    suspend fun deleteAll()

    @Query("select * from MsApi1 order by api1ID asc")
    fun getMsApi1(): LiveData<List<MsApi1>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMsApi1(msApi1: MsApi1)

    @Query("update MsApi1 set latitude = :latitude, longitude = :longitude, method = :method, month = :month, year = :year ")
    suspend fun updateMsApi1(prayerID: Int,latitude: String, longitude: String, method: String, month: String, year:String)

}