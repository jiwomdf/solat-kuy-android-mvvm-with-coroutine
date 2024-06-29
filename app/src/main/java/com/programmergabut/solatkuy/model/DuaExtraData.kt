package com.programmergabut.solatkuy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DuaExtraData(
    val duaTitle: String,
    val duaAr: String,
    val duaLt: String,
    val duaEn: String,
    val duaIn: String,
    val duaRef: String
): Parcelable