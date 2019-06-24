package com.deepak.internetspeed.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(DailyConsumption::class), version = 1)
public abstract class ConsumptionDatabase : RoomDatabase(){
    abstract fun consumptionDAO() : ConsumptionDAO

    companion object{
        @Volatile
        private var INSTANCE : ConsumptionDatabase? = null

        // Singleton pattern. Returns a single instance of ConsumptionDatabase
        fun getDatabase(context: Context) : ConsumptionDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConsumptionDatabase::class.java,
                    "consumption_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}