package com.deepak.internetspeed

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.deepak.internetspeed.database.DailyConsumption
import com.deepak.internetspeed.services.TrafficStatusService
import com.deepak.internetspeed.util.DateUtils
import com.deepak.internetspeed.viewmodels.ConsumptionViewModel

class MainActivity : AppCompatActivity() {

    val TAG = "MAIN_ACTVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        val intentService = Intent(this@MainActivity,TrafficStatusService::class.java)
        intentService.setPackage(this.packageName)
        startService(intentService)
        getTodaysUsage()

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(getString(R.string.channel_id), name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getTodaysUsage(){
        Log.e(TAG,"DAY ID: ${DateUtils.getDayID()}")
        val viewModel = ViewModelProviders.of(this).get(ConsumptionViewModel::class.java)
        viewModel.getDayUsage().observe(this, Observer<DailyConsumption>{
            if( it != null) {
                Log.d(TAG, "USAGE: ${it.total}")
            }
        })
    }

}
