package com.deepak.internetspeed.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.deepak.internetspeed.database.ConsumptionDAO
import com.deepak.internetspeed.database.DailyConsumption

class ConsumptionRepository(final val consumptionDAO: ConsumptionDAO){

    val allConsumptions : LiveData<List<DailyConsumption>> = consumptionDAO.getAll()

    @WorkerThread
    suspend fun insert(dailyConsumption : DailyConsumption){
        consumptionDAO.insert(dailyConsumption)
    }
}