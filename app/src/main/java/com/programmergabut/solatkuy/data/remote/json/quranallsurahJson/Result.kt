package com.programmergabut.solatkuy.data.remote.json.quranallsurahJson


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("englishName")
    val englishName: String,
    @SerializedName("englishNameLC")
    var englishNameLowerCase: String?,
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