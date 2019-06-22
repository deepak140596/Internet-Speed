package com.deepak.internetspeed.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.deepak.internetspeed.services.TrafficStatusService

class BReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        var intentService = Intent(context,TrafficStatusService::class.java)
        context!!.startService(intentService)
    }

}