package com.programmergabut.solatkuy.data.model.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notified_prayer")
class PrayerLocal(val prayerName: String, val isNotified: Boolean){

    constructor(prayerID: Int, prayerName: String, isNotified: Boolean): this(prayerName,isNotified)

    @PrimaryKey(autoGenerate = true)
    var prayerID: Int = 0
}