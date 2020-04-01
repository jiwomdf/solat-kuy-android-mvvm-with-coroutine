package com.programmergabut.solatkuy.data.model.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notified_prayer")
class PrayerLocal(val prayerName: String, val isNotified: Boolean, val prayerTime: String){

    @PrimaryKey(autoGenerate = true)
    var prayerID: Int = 0

    constructor(prayerID: Int,  prayerName: String,  isNotified: Boolean,  prayerTime: String): this (prayerName, isNotified, prayerTime){
        this.prayerID = prayerID
    }

}