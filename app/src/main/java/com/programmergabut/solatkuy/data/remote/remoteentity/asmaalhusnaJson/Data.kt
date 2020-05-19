package com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("en")
    val en: En,
    @SerializedName("name")
    val name: String,
    @SerializedName("number")
    val number: Int,
    @SerializedName("transliteration")
    val transliteration: String
)