package com.programmergabut.solatkuy.data.local.localentity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "fast_rata_items")
data class FastRataItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)