package com.programmergabut.solatkuy.data.local.localentity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MsFavAyah")
class MsFavAyah(val surahID: Int?,
                @PrimaryKey val ayahID: Int?,
                val ayahAr: String?,
                val ayahEn: String?)

