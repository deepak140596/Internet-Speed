package com.deepak.internetspeed.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ConsumptionDAO{

    @Query("SELECT * FROM daily_consumption ORDER BY timestamp DESC")
    fun getAll() : LiveData<List<DailyConsumption>>

    //@Query("SELECT * FROM daily_consumption")

    @Insert
    suspend fun insert(dailyConsumption: DailyConsumption)
}