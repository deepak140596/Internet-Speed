package com.deepak.internetspeed.util

import android.content.Context
import android.net.TrafficStats
import java.util.*
import android.net.wifi.WifiManager

class TrafficUtils{

    companion object{
        val GB : Long = 1000000000
        val MB : Long = 1000000
        val KB : Long = 1000

        fun getDownloadSpeed() : String{

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

            if (mDownloadSpeed >= GB) {
                mDownloadSpeedWithDecimals = mDownloadSpeed.toFloat() / GB.toFloat()
                units = " GB"
            } else if (mDownloadSpeed >= MB) {
                mDownloadSpeedWithDecimals = mDownloadSpeed.toFloat() / MB.toFloat()
                units = " MB"

            } else {
                mDownloadSpeedWithDecimals = mDownloadSpeed.toFloat() / KB.toFloat()
                units = " KB"
            }


            downloadSpeedOutput = if (units != " KB" && mDownloadSpeedWithDecimals < 100) {
                String.format(Locale.US, "%.1f", mDownloadSpeedWithDecimals)
            } else {
                Integer.toString(mDownloadSpeedWithDecimals.toInt())
            }

            return (downloadSpeedOutput + units)

        }

        fun convertToBytes(value: Float, unit: String) : Long{
            if(unit == "KB"){
                return (value.toLong())* KB
            } else if(unit == "MB"){
                return (value.toLong())* MB
            } else if(unit == "GB"){
                return (value.toLong()) * GB
            }
            return 0
        }

        fun isWifiConnected(context: Context) : Boolean{
            val wifiMgr = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?

            if (wifiMgr!!.isWifiEnabled) { // Wi-Fi adapter is ON

                val wifiInfo = wifiMgr.connectionInfo

                return wifiInfo.networkId != -1

            } else {
                return false // Wi-Fi adapter is OFF
            }
        }
    }
}