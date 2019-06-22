package com.deepak.internetspeed.services

import android.app.IntentService
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.TrafficStats
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import java.util.*

class TSService : IntentService("TrafficStatusService") {

    lateinit var handler: Handler
    override fun onHandleIntent(intent: Intent?) {

        initializeHandler()
        getDownloadSpeed()
        val completeMessage = handler.obtainMessage(1)
        completeMessage.sendToTarget()
    }

    fun initializeHandler(){

        handler = object : Handler(Looper.getMainLooper()) {

            override fun handleMessage(inputMessage: Message) {

            }
        }
    }

    private fun getDownloadSpeed() : String{

        var downloadSpeedOutput = ""
        var units = ""
        val mRxBytesPrevious = TrafficStats.getTotalRxBytes()

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val mRxBytesCurrent = TrafficStats.getTotalRxBytes()

        val mDownloadSpeed = mRxBytesCurrent - mRxBytesPrevious

        val mDownloadSpeedWithDecimals: Float

        if (mDownloadSpeed >= 1000000000) {
            mDownloadSpeedWithDecimals = mDownloadSpeed.toFloat() / 1000000000.toFloat()
            units = " GB"
        } else if (mDownloadSpeed >= 1000000) {
            mDownloadSpeedWithDecimals = mDownloadSpeed.toFloat() / 1000000.toFloat()
            units = " MB"

        } else {
            mDownloadSpeedWithDecimals = mDownloadSpeed.toFloat() / 1000.toFloat()
            units = " KB"
        }


        if (units != " KB" && mDownloadSpeedWithDecimals < 100) {
            downloadSpeedOutput = String.format(Locale.US, "%.1f", mDownloadSpeedWithDecimals)
        } else {
            downloadSpeedOutput = Integer.toString(mDownloadSpeedWithDecimals.toInt())
        }

        Log.d("TSS0",downloadSpeedOutput)
        return downloadSpeedOutput

    }

}