package com.programmergabut.solatkuy.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * Created by Katili Jiwo Adi Wiyono on 27/03/20.
 */


@Entity(tableName = "notified_prayer")
class PrayerLocal (val prayerName: String,
                  val isNotified: Boolean,
                  val prayerTime: String){

    @PrimaryKey(autoGenerate = true)
    var prayerID: Int = 0

    constructor(prayerID: Int,  prayerName: String,  isNotified: Boolean,  prayerTime: String): this (prayerName, isNotified, prayerTime){
        this.prayerID = prayerID
    }

}