package com.programmergabut.solatkuy.data.local.localentity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "MsAyah")
data class MsAyah(
    val hizbQuarter: Int,
    val juz: Int,
    val manzil: Int,
    val number: Int,
    val numberInSurah: Int,
    val page: Int,
    val ruku: Int,
    val text: String,
    val englishName: String,
    val englishNameTranslation: String,
    val name: String,
    val numberOfAyahs: Int,
    val revelationType: String,
    var textEn: String? = "",
    var isFav: Boolean = false,
    var isLastRead: Boolean = false,
    var surahID: Int,
){
    @PrimaryKey(autoGenerate = true)
    var ayahID: Int = 0
}