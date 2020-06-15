package com.programmergabut.solatkuy.data.local.localentity

import androidx.room.Entity

@Entity(tableName = "MsFavAyah", primaryKeys = ["surahID", "ayahID"])
class MsFavAyah(val surahID: Int,
                val ayahID: Int,
                val surahName: String?,
                val ayahAr: String?,
                val ayahEn: String?)

