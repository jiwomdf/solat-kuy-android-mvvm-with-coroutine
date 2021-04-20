package com.programmergabut.solatkuy.data.remote.json.readsurahJsonAr


import com.google.gson.annotations.SerializedName

data class Ayah(
    @SerializedName("hizbQuarter")
    val hizbQuarter: Int,
    @SerializedName("juz")
    val juz: Int,
    @SerializedName("manzil")
    val manzil: Int,
    @SerializedName("number")
    val number: Int,
    @SerializedName("numberInSurah")
    val numberInSurah: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("ruku")
    val ruku: Int,
    @SerializedName("text")
    val text: String,

    var textEn: String?,
    var isFav: Boolean,
    var isLastRead: Boolean
)