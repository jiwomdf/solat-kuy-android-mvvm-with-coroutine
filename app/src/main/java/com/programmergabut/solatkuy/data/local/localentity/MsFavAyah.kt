package com.programmergabut.solatkuy.data.local.localentity

import androidx.room.Entity

@Entity(tableName = "ms_fav_ayah", primaryKeys = ["surahID", "ayahID"])
data class MsFavAyah(val surahID: Int,
                val ayahID: Int,
                val surahName: String?,
                val ayahAr: String?,
                val ayahEn: String?)

