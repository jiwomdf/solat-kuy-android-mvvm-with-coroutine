package com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson


import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("latitude")
    var latitude: Double,
    @SerializedName("latitudeAdjustmentMethod")
    var latitudeAdjustmentMethod: String,
    @SerializedName("longitude")
    var longitude: Double,
    @SerializedName("method")
    var method: Method,
    @SerializedName("midnightMode")
    var midnightMode: String,
    @SerializedName("offset")
    var offset: Offset,
    @SerializedName("school")
    var school: String,
    @SerializedName("timezone")
    var timezone: String
)