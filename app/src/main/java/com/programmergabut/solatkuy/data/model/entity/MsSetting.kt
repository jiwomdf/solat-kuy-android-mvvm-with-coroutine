package com.programmergabut.solatkuy.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MsSetting")
class MsSetting(@PrimaryKey val no: Int = 1, val isHasOpenApp: Boolean)