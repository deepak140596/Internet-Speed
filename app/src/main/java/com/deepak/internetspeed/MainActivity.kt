package com.deepak.internetspeed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.deepak.internetspeed.services.TrafficStatusService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val intentService = Intent(this@MainActivity,TrafficStatusService::class.java)
        intentService.setPackage(this.packageName)
        startService(intentService)
    }


}
