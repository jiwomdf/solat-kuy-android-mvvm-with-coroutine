package com.programmergabut.solatkuy.data.local.localentity

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * Created by Katili Jiwo Adi Wiyono on 27/03/20.
 */


@Entity(tableName = "MsApi1")
class MsApi1 (@PrimaryKey var api1ID: Int = 0,
                                 val latitude: String,
                                 val longitude: String,
                                 val method: String,
                                 val month: String,
                                 val year: String)