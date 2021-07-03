package com.programmergabut.solatkuy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.programmergabut.solatkuy.data.local.localentity.MsApi1

/*
 * Created by Katili Jiwo Adi Wiyono on 27/03/20.
 */


@Dao
interface MsApi1Dao {

    @Query("select * from ms_api_1")
    fun observeMsApi1(): LiveData<MsApi1>

    @Query("select * from ms_api_1")
    fun getMsApi1(): MsApi1?

    @Query("delete from ms_api_1")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMsApi1(msApi1: MsApi1)

    @Query("update ms_api_1 set latitude = :latitude, longitude = :longitude, method = :method, month = :month, year = :year where api1ID = :api1ID")
    suspend fun updateMsApi1(api1ID: Int, latitude: String, longitude: String, method: String, month: String, year:String)

    @Query("update ms_api_1 set method = :method where api1ID = :api1ID")
    suspend fun updateMsApi1Method(api1ID: Int, method: String)


    @Query("update ms_api_1 set month = :month, year = :year where api1ID = :api1ID")
    suspend fun updateMsApi1MonthAndYear(api1ID: Int, month: String, year:String)

}