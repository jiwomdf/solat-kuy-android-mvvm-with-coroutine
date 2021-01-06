package com.programmergabut.solatkuy.data.local.localentity

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * Created by Katili Jiwo Adi Wiyono on 27/03/20.
 */


@Entity(tableName = "notified_prayer")
class NotifiedPrayer (
    var prayerName: String,
    var isNotified: Boolean,
    var prayerTime: String){

    @PrimaryKey(autoGenerate = true)
    var prayerID: Int = 0

    constructor(prayerID: Int,  prayerName: String,  isNotified: Boolean,  prayerTime: String): this (prayerName, isNotified, prayerTime){
        this.prayerID = prayerID
    }

}