package com.deepak.internetspeed.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ConsumptionDAO{

    @Query("SELECT * FROM daily_consumption ORDER BY timestamp DESC limit 30")
    fun getAll() : LiveData<List<DailyConsumption>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dailyConsumption: DailyConsumption)

    @Query("SELECT * from daily_consumption where dayID = :dayID")
    fun getDayUsage(dayID: String) : LiveData<DailyConsumption>

    @Query("SELECT * from daily_consumption where dayID = :dayID")
    fun getDayUsageInBackgroundThread(dayID: String) : DailyConsumption

    @Query("UPDATE daily_consumption SET wifi = wifi + :wifi, total = total + :wifi WHERE dayID = :dayID")
    suspend fun updateWifiUsage( dayID : String, wifi : Long)

    @Query("UPDATE daily_consumption SET mobile = mobile + :mobile, total = total + :mobile WHERE dayID = :dayID")
    suspend fun updateMobileUsage( dayID : String, mobile : Long)

}