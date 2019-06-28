package com.deepak.internetspeed.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ConsumptionDAO{

    @Query("SELECT * FROM daily_consumption ORDER BY timestamp DESC")
    fun getAll() : LiveData<List<DailyConsumption>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dailyConsumption: DailyConsumption)

    @Query("SELECT * from daily_consumption where timestamp = :timestamp")
    fun getDayUsage(timestamp: String) : LiveData<DailyConsumption>

    @Query("UPDATE daily_consumption SET wifi = wifi + :wifi, total = total + :wifi WHERE timestamp = :timestamp")
    suspend fun updateWifiUsage( timestamp : String, wifi : Long)

    @Query("UPDATE daily_consumption SET mobile = mobile + :mobile, total = total + :mobile WHERE timestamp = :timestamp")
    suspend fun updateMobileUsage( timestamp : String, mobile : Long)

}