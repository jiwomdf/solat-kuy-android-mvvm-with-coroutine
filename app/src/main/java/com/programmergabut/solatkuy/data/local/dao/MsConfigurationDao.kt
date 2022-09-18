package com.programmergabut.solatkuy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.programmergabut.solatkuy.data.local.localentity.MsConfiguration

/*
 * Created by Katili Jiwo Adi Wiyono on 27/03/20.
 */


@Dao
interface MsConfigurationDao {

    @Query("select * from ms_configuration")
    fun observeMsConfiguration(): LiveData<MsConfiguration>

    @Query("select * from ms_configuration")
    fun getMsConfiguration(): MsConfiguration?

    @Query("delete from ms_configuration")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMsConfiguration(msConfiguration: MsConfiguration)

    @Query("update ms_configuration set latitude = :latitude, longitude = :longitude, method = :method, month = :month, year = :year where api1ID = :api1ID")
    suspend fun updateMsConfiguration(api1ID: Int, latitude: String, longitude: String, method: String, month: String, year:String)

    @Query("update ms_configuration set method = :method where api1ID = :api1ID")
    suspend fun updateMsConfigurationMethod(api1ID: Int, method: String)


    @Query("update ms_configuration set month = :month, year = :year where api1ID = :api1ID")
    suspend fun updateMsConfigurationMonthAndYear(api1ID: Int, month: String, year:String)

}