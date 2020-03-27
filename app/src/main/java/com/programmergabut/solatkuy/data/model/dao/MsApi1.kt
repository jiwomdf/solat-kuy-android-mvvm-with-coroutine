package com.programmergabut.solatkuy.data.model.dao

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "MsApi1")
class MsApi1(@PrimaryKey() var api1ID: Int = 0,
             val latitude: String,
             val longitude: String,
             val method: String,
             val month: String,
             val year: String)