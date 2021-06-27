package com.programmergabut.solatkuy.data.local.localentity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ms_calculation_method")
data class MsCalculationMethods(
    @PrimaryKey
    var index: Int,
    val name: String,
    val method: Int
)