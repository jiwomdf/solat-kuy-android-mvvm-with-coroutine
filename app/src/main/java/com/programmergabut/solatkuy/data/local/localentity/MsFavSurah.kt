package com.programmergabut.solatkuy.data.local.localentity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MsFavSurah")
class MsFavSurah (@PrimaryKey val surahID: Int?,
                  val surahName: String?,
                  val surahTranslation: String?)