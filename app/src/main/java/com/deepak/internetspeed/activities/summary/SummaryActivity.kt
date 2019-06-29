package com.deepak.internetspeed.activities.summary

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepak.internetspeed.R
import com.deepak.internetspeed.database.DailyConsumption
import com.deepak.internetspeed.services.TrafficStatusService
import com.deepak.internetspeed.util.ChartUtils
import com.deepak.internetspeed.util.DateUtils
import com.deepak.internetspeed.util.TrafficUtils
import com.deepak.internetspeed.viewmodels.ConsumptionViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_graph_backdrop.*

class SummaryActivity : AppCompatActivity() {

    val TAG = "MAIN_ACTVITY"
    lateinit var adapter: RowDailyUsageAdapter
    lateinit var viewModel: ConsumptionViewModel
    var allUsage : List<DailyConsumption> = emptyList()
    var chartUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        val intentService = Intent(this@SummaryActivity,TrafficStatusService::class.java)
        intentService.setPackage(this.packageName)
        startService(intentService)

        setupRecyclerView()
        getAllUsage()

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.channel_id), name, importance)
            channel.description = description
            channel.setSound(null,null)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getAllUsage(){
        viewModel = ViewModelProviders.of(this).get(ConsumptionViewModel::class.java)
        viewModel.allConsumptions.observe(this, Observer<List<DailyConsumption>>{ dailyConsumption ->
            if( dailyConsumption != null) {
                allUsage = dailyConsumption
                adapter.allUsage = allUsage
                adapter.notifyDataSetChanged()

                setupUsageStats(allUsage)
                val networkEntries = ChartUtils.getDailyMobileConsumptionEntries(allUsage)
                if(!chartUpdated) {
                    setupLineChart(networkEntries)
                    chartUpdated = true
                }
            }
        })
    }

    fun setupRecyclerView(){
        activity_main_recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = RowDailyUsageAdapter(allUsage)
        activity_main_recycler_view.adapter = adapter
    }

    fun setupLineChart(entries: List<Entry>){
        custom_graph_line_chart.invalidate()

        try{
            var lineDataset = LineDataSet(entries,"Usage")

            var lineData = LineData(lineDataset)
            var xAxis = custom_graph_line_chart.xAxis
            xAxis.setLabelCount(0,true)
            custom_graph_line_chart.animateX(1000)
            custom_graph_line_chart.data = lineData

        } catch (e : Exception){
            Log.e(TAG,"Exception : $e")
        }
    }

    fun setupUsageStats(listDailyConsumption: List<DailyConsumption>){
        val dailyMobile = listDailyConsumption[0].mobile
        val dailyWifi = listDailyConsumption[0].wifi
        val monthlyMobile = TrafficUtils.getMonthlyMobileUsage(listDailyConsumption)
        val monthlyWifi = TrafficUtils.getMonthlyWifiUsage(listDailyConsumption)

        custom_graph_day_used_mobile_tv.text = TrafficUtils.getMetricData(dailyMobile)
        custom_graph_day_used_wifi_tv.text = TrafficUtils.getMetricData(dailyWifi)
        custom_graph_monthly_used_mobile_tv.text = TrafficUtils.getMetricData(monthlyMobile)
        custom_graph_monthly_used_wifi_tv.text = TrafficUtils.getMetricData(monthlyWifi)
    }

    override fun onResume() {
        super.onResume()
        chartUpdated = false
    }

}
