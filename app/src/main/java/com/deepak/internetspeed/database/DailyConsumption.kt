package com.deepak.internetspeed.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_consumption")
data class DailyConsumption(
    @PrimaryKey
    var timestamp: String,

    @ColumnInfo(name = "mobile")
    var mobile : Long,

    @ColumnInfo(name = "wifi")
    var wifi : Long,

    @ColumnInfo(name = "total")
    var total : Long
)