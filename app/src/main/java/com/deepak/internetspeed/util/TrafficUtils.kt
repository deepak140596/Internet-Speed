package com.deepak.internetspeed.util

import android.content.Context
import android.net.TrafficStats
import java.util.*
import android.net.wifi.WifiManager
import com.deepak.internetspeed.database.DailyConsumption

class TrafficUtils{

    companion object{
        val GB : Long = 1000000000
        val MB : Long = 1000000
        val KB : Long = 1000

        fun getNetworkSpeed() : String{

            var downloadSpeedOutput = ""
            var units = ""
            val mBytesPrevious = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxPackets()

            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            val mBytesCurrent = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxPackets()

            val mNetworkSpeed = mBytesCurrent - mBytesPrevious

            val mDownloadSpeedWithDecimals: Float

            if (mNetworkSpeed >= GB) {
                mDownloadSpeedWithDecimals = mNetworkSpeed.toFloat() / GB.toFloat()
                units = " GB"
            } else if (mNetworkSpeed >= MB) {
                mDownloadSpeedWithDecimals = mNetworkSpeed.toFloat() / MB.toFloat()
                units = " MB"

            } else {
                mDownloadSpeedWithDecimals = mNetworkSpeed.toFloat() / KB.toFloat()
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

        fun getMetricData(bytes : Long) : String{
            var dataWithDecimals : Float
            var units : String
            if (bytes >= GB) {
                dataWithDecimals = bytes.toFloat() / GB.toFloat()
                units = " GB"
            } else if (bytes >= MB) {
                dataWithDecimals = bytes.toFloat() / MB.toFloat()
                units = " MB"

            } else {
                dataWithDecimals = bytes.toFloat() / KB.toFloat()
                units = " KB"
            }


            var output=  if (units != " KB" && dataWithDecimals < 100) {
                String.format(Locale.US, "%.1f", dataWithDecimals)
            } else {
                Integer.toString(dataWithDecimals.toInt())
            }

            return output + units
        }

        fun getMonthlyWifiUsage(listDailyConsumption : List<DailyConsumption>): Long {
            var total : Long = 0L
            for (dailyConsumption in listDailyConsumption){
                total += dailyConsumption.wifi
            }

            return total
        }

        fun getMonthlyMobileUsage(listDailyConsumption : List<DailyConsumption>): Long {
            var total : Long = 0L
            for (dailyConsumption in listDailyConsumption){
                total += dailyConsumption.mobile
            }

            return total
        }

        fun getMonthlyUsage(listDailyConsumption : List<DailyConsumption>): Long {
            var total : Long = 0L
            for (dailyConsumption in listDailyConsumption){
                total += dailyConsumption.total
            }

            return total
        }

    }
}