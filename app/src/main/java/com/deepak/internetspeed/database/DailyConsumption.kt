package com.deepak.internetspeed.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_consumption")
data class DailyConsumption(
    @PrimaryKey
    var dayID: String,

    @ColumnInfo(name = "timestamp")
    var timestamp : Long,

    @ColumnInfo(name = "mobile")
    var mobile : Long,

    @ColumnInfo(name = "wifi")
    var wifi : Long,

    @ColumnInfo(name = "total")
    var total : Long
)