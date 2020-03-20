package com.programmergabut.solatkuy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notified_prayer")
class PrayerLocal(val prayerName: String, val isNotified: Boolean){
    @PrimaryKey(autoGenerate = true)
    var prayerID: Int = 0
}