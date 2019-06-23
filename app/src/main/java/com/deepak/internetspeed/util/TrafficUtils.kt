package com.deepak.internetspeed.util

import android.net.TrafficStats
import java.util.*

class TrafficUtils{

    companion object{
        val GB = 1000000000
        val MB = 1000000
        val KB = 1000

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
    }
}