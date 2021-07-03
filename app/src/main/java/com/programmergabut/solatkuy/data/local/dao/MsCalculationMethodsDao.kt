package com.programmergabut.solatkuy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.programmergabut.solatkuy.data.local.localentity.MsAyah
import com.programmergabut.solatkuy.data.local.localentity.MsCalculationMethods

@Dao
interface MsCalculationMethodsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: List<MsCalculationMethods>)

    @Query("DELETE FROM ms_calculation_method")
    fun delete()

    @Query("SELECT * FROM ms_calculation_method")
    fun getMethods(): LiveData<List<MsCalculationMethods>>
}