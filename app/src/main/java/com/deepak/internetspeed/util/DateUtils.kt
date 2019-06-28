package com.deepak.internetspeed.util

import java.text.SimpleDateFormat

class DateUtils {
    companion object{
        var TAG = "DATE_UTILS"

        fun getDayID() : String{

            var timestamp = System.currentTimeMillis()
            var formatter = SimpleDateFormat("dd-MMM-yyyy")
            return formatter.format(timestamp)
        }
    }
}