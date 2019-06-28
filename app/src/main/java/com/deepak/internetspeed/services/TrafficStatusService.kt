package com.deepak.internetspeed.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import androidx.lifecycle.LifecycleObserver
import com.deepak.internetspeed.MainActivity
import com.deepak.internetspeed.R
import com.deepak.internetspeed.database.DailyConsumption
import com.deepak.internetspeed.util.DateUtils
import com.deepak.internetspeed.util.ImageUtils
import com.deepak.internetspeed.util.TrafficUtils
import com.deepak.internetspeed.viewmodels.ConsumptionViewModel
import java.util.*

class TrafficStatusService : IntentService("TrafficStatusService"), LifecycleObserver{
    private var timer : Timer = Timer()
    private val DELAY = 0L
    private val DURATION = 1000L
    private val NOTIFICATION_ID = 1
    lateinit var notificationBuilder : Notification.Builder
    lateinit var notificationManager: NotificationManager

    lateinit var consumptionViewModel: ConsumptionViewModel

    override fun onHandleIntent(intent: Intent?) {

    }

    override fun onCreate() {
        super.onCreate()

        consumptionViewModel = ConsumptionViewModel(application)

        createNotification()

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                var downloadSpeed = TrafficUtils.getDownloadSpeed()
                saveToDB(downloadSpeed)
                updateNotification(downloadSpeed)
            }
        }, DELAY, DURATION)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        val intent = Intent("RestartService")
        sendBroadcast(intent)
    }

    fun updateNotification(downloadSpeed : String){
        val speed = downloadSpeed.subSequence(0, downloadSpeed.indexOf(" ")+1)
        val units = downloadSpeed.subSequence(downloadSpeed.indexOf(" ")+1,downloadSpeed.length)

        val bitmap = ImageUtils.createBitmapFromString(speed.toString(), units.toString())
        val icon = Icon.createWithBitmap(bitmap)
        notificationBuilder.setSmallIcon(icon)
        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build())
    }

    fun saveToDB(downloadSpeed: String){
        val speed : String = (downloadSpeed.subSequence(0, downloadSpeed.indexOf(" ")+1)).toString()
        val units : String  = (downloadSpeed.subSequence(downloadSpeed.indexOf(" ")+1,downloadSpeed.length)).toString()

        var dailyConsumption = DailyConsumption(DateUtils.getDayID(),0,0,0)
        consumptionViewModel.insert(dailyConsumption)

        val toBytes = TrafficUtils.convertToBytes(speed.toFloat(),units)

        if(TrafficUtils.isWifiConnected(this)){
            consumptionViewModel.updateWifiUsage(dailyConsumption.timestamp,toBytes)
        } else {
            consumptionViewModel.updateMobileUsage(dailyConsumption.timestamp,toBytes)
        }
    }

    fun createNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = Notification.Builder(this,getString(R.string.channel_id))
        } else{
            @Suppress("DEPRECATION")
            notificationBuilder = Notification.Builder(this)
        }

        notificationBuilder.setContentTitle("")
        notificationBuilder.setSmallIcon(Icon.createWithBitmap(ImageUtils.createBitmapFromString("0", " KB")))
        notificationBuilder.setVisibility(Notification.VISIBILITY_SECRET)
        notificationBuilder.setOngoing(true)

        var intent = Intent(this,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        var pendingIntent = PendingIntent.getActivity(this,0,intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        notificationBuilder.setContentIntent(pendingIntent)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

}