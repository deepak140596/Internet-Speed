package com.deepak.internetspeed.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_consumption")
data class DailyConsumption(
    @PrimaryKey
    var timestamp: Long,

    @ColumnInfo(name = "mobile")
    var mobile : Float,

    @ColumnInfo(name = "wifi")
    var wifi : Float,

    @ColumnInfo(name = "total")
    var total : Float
)