package com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("ayahs")
    var ayahs: List<Ayah>,
    @SerializedName("edition")
    val edition: Edition,
    @SerializedName("englishName")
    val englishName: String,
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