package com.programmergabut.solatkuy.data.local.localentity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ms_setting")
class MsSetting(@PrimaryKey val no: Int = 1,
                val isHasOpenApp: Boolean,
                val isUsingDBQuotes: Boolean)