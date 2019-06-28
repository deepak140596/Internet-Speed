package com.deepak.internetspeed.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.deepak.internetspeed.database.ConsumptionDAO
import com.deepak.internetspeed.database.DailyConsumption
import com.deepak.internetspeed.util.DateUtils

class ConsumptionRepository(val consumptionDAO: ConsumptionDAO){

    val allConsumptions : LiveData<List<DailyConsumption>> = consumptionDAO.getAll()

    val getDayUsage : LiveData<DailyConsumption> = consumptionDAO.getDayUsage(DateUtils.getDayID())

    @WorkerThread
    suspend fun insert(dailyConsumption : DailyConsumption){
        consumptionDAO.insert(dailyConsumption)
    }

    @WorkerThread
    suspend fun updateWifiUsage(timestamp : String, wifi : Long){
        consumptionDAO.updateWifiUsage(timestamp, wifi)
    }

    @WorkerThread
    suspend fun updateMobileUsage(timestamp : String, mobile : Long){
        consumptionDAO.updateMobileUsage(timestamp, mobile)
    }

}