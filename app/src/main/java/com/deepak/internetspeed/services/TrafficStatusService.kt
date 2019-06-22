package com.deepak.internetspeed.services

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.util.Log
import com.deepak.internetspeed.util.TrafficUtils
import java.util.*

class TrafficStatusService : IntentService("TrafficStatusService"){
    private var timer : Timer = Timer()
    private val DELAY = 0L
    private val DURATION = 1000L

    override fun onHandleIntent(intent: Intent?) {

    }

    override fun onCreate() {
        super.onCreate()

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Log.d("TSS",TrafficUtils.getDownloadSpeed())
            }
        }, DELAY, DURATION)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

}