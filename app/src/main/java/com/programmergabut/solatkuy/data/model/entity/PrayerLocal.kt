package com.programmergabut.solatkuy.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notified_prayer")
class PrayerLocal(val prayerName: String, val isNotified: Boolean, val prayerTime: String){

    @PrimaryKey(autoGenerate = true)
    var prayerID: Int = 0
}