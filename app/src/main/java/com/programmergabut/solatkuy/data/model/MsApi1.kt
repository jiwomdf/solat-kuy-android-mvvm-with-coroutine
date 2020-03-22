package com.programmergabut.solatkuy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MsApi1")
class MsApi1(val latitude: String, val longitude: String, val method: String, val month: String, val year: String){

    @PrimaryKey(autoGenerate = true)
    var api1ID: Int = 0
}