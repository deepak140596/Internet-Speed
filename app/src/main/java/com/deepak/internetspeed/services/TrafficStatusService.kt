package com.deepak.internetspeed.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.RemoteViews
import androidx.lifecycle.*
import com.deepak.internetspeed.activities.summary.SummaryActivity
import com.deepak.internetspeed.R
import com.deepak.internetspeed.database.DailyConsumption
import com.deepak.internetspeed.database.SharedPreferenceDB
import com.deepak.internetspeed.util.DateUtils
import com.deepak.internetspeed.util.ImageUtils
import com.deepak.internetspeed.util.TrafficUtils
import com.deepak.internetspeed.viewmodels.ConsumptionViewModel
import java.util.*

class TrafficStatusService : IntentService("TrafficStatusService"), LifecycleOwner{

    private var timer : Timer = Timer()
    private val DELAY = 0L
    private val DURATION = 1000L
    private val NOTIFICATION_ID = 1
    lateinit var notificationBuilder : Notification.Builder
    lateinit var notificationManager: NotificationManager

    lateinit var consumptionViewModel: ConsumptionViewModel
    val registry : LifecycleRegistry = LifecycleRegistry(this)

    lateinit var notificationLayout : RemoteViews

    override fun onHandleIntent(intent: Intent?) {

    }

    override fun onCreate() {
        super.onCreate()

        registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        consumptionViewModel = ConsumptionViewModel(application)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotification()

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                var downloadSpeed = TrafficUtils.getNetworkSpeed()

                saveToDB(downloadSpeed)
                showNotificationIfEnabled(downloadSpeed)
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

    override fun getLifecycle(): Lifecycle {
        return lifecycle
    }

    fun showNotificationIfEnabled(downloadSpeed: String){
        val speed : String = (downloadSpeed.subSequence(0, downloadSpeed.indexOf(" ")+1)).toString()

        if(SharedPreferenceDB.isNotificationEnabled(this)){
            if(speed.toFloat() != 0F){
                startForeground(NOTIFICATION_ID, notificationBuilder.build())
                updateNotification(downloadSpeed)
            } else {
                stopForeground(true)
                notificationManager.cancel(NOTIFICATION_ID)
            }
        } else {
            stopForeground(true)
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }

    fun updateNotification(downloadSpeed : String){
        val speed = downloadSpeed.subSequence(0, downloadSpeed.indexOf(" ")+1)
        val units = downloadSpeed.subSequence(downloadSpeed.indexOf(" ")+1,downloadSpeed.length)

        val bitmap = ImageUtils.createBitmapFromString(speed.toString(), units.toString())
        val icon = Icon.createWithBitmap(bitmap)
        notificationBuilder.setSmallIcon(icon)

        // set notification details : Speed, mobile and wifi usage
        notificationLayout.setTextViewText(R.id.custom_notification_speed_tv,"$downloadSpeed/s")
        val it = consumptionViewModel.getDayUsageInBackgroundThread(DateUtils.getDayID())

        if(it != null) {
            notificationLayout.setTextViewText(
                R.id.custom_notification_mobile_tv,
                TrafficUtils.getMetricData(it.mobile)
            )
            notificationLayout.setTextViewText(
                R.id.custom_notification_wifi_tv,
                TrafficUtils.getMetricData(it.wifi))
        }

        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build())
    }

    fun saveToDB(downloadSpeed: String){
        val speed : String = (downloadSpeed.subSequence(0, downloadSpeed.indexOf(" ")+1)).toString()
        val units : String  = (downloadSpeed.subSequence(downloadSpeed.indexOf(" ")+1,downloadSpeed.length)).toString()

        var dailyConsumption = DailyConsumption(DateUtils.getDayID(),System.currentTimeMillis(),0,0,0)

        // Insert or ignore
        consumptionViewModel.insert(dailyConsumption)

        val toBytes = TrafficUtils.convertToBytes(speed.toFloat(),units)

        if(TrafficUtils.isWifiConnected(this)){
            consumptionViewModel.updateWifiUsage(dailyConsumption.dayID,toBytes)
        } else {
            consumptionViewModel.updateMobileUsage(dailyConsumption.dayID,toBytes)
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
        notificationBuilder.setVisibility(Notification.VISIBILITY_PUBLIC)
        notificationBuilder.setOngoing(true)
        notificationBuilder.setAutoCancel(true)
        setNotificationContent()
        notificationBuilder.setContentIntent(createPendingIntent())

        //notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

        //startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun setNotificationContent(){
        notificationLayout = RemoteViews("com.deepak.internetspeed",R.layout.custom_notification_view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationBuilder.setCustomContentView(notificationLayout)
        }else{
            notificationBuilder.setContent(notificationLayout)
        }
    }

    fun createPendingIntent(): PendingIntent? {
        var intent = Intent(this, SummaryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        var pendingIntent = PendingIntent.getActivity(this,0,intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        return pendingIntent
    }

}