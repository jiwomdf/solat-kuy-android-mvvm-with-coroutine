package com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("englishName")
    val englishName: String,
    @SerializedName("englishNameLC")
    var englishNameLC: String?,
    @SerializedName("englishNameTranslation")
    val englishNameTranslation: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("number")
    val number: Int,
    @SerializedName("numberOfAyahs")
    val numberOfAyahs: Int,
    @SerializedName("revelationType")
    val revelationType: String
)