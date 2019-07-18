package com.deepak.internetspeed.activities.summary

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.deepak.internetspeed.R
import com.deepak.internetspeed.database.DailyConsumption
import com.deepak.internetspeed.services.TrafficStatusService
import com.deepak.internetspeed.util.ChartUtils
import com.deepak.internetspeed.util.TrafficUtils
import com.deepak.internetspeed.viewmodels.ConsumptionViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_graph_backdrop.*
import android.widget.AdapterView.OnItemSelectedListener

import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.deepak.internetspeed.activities.settings.SettingsActivity
import com.deepak.internetspeed.database.SharedPreferenceDB
import com.deepak.internetspeed.util.LargeValueFormatterBytes


class SummaryActivity : AppCompatActivity() {

    val TAG = "MAIN_ACTVITY"
    lateinit var adapter: RowDailyUsageAdapter
    lateinit var viewModel: ConsumptionViewModel
    var allUsage : List<DailyConsumption> = emptyList()

    var ischartUpdated = false
    var isWifiSelected = false

    lateinit var spinner : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        val intentService = Intent(this@SummaryActivity,TrafficStatusService::class.java)
        intentService.setPackage(this.packageName)
        startService(intentService)

        setupRecyclerView()
        setupSpinner()
        getAllUsage()
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.channel_id), name, importance)
            channel.description = description
            channel.setSound(null,null)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getAllUsage(){
        viewModel = ViewModelProviders.of(this).get(ConsumptionViewModel::class.java)
        viewModel.allConsumptions.observe(this, Observer<List<DailyConsumption>>{ dailyConsumption ->
            if( dailyConsumption != null && dailyConsumption.isNotEmpty()) {
                allUsage = dailyConsumption
                adapter.allUsage = allUsage
                adapter.notifyDataSetChanged()

                setupUsageStats(allUsage)
                val networkEntries : List<Entry>
                if(!isWifiSelected) {
                    networkEntries = ChartUtils.getDailyMobileConsumptionEntries(allUsage)
                }else{
                    networkEntries = ChartUtils.getDailyWifiConsumptionEntries(allUsage)
                }
                if(!ischartUpdated) {
                    setupLineChart(networkEntries)
                    ischartUpdated = true
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
            lineDataset.fillColor = Color.LTGRAY
            lineDataset.setDrawFilled(true)
            lineDataset.valueTextColor = Color.WHITE

            var lineData = LineData(lineDataset)
            lineData.setValueFormatter(LargeValueFormatterBytes())

            custom_graph_line_chart.description.isEnabled = false

            var xAxis = custom_graph_line_chart.xAxis
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawLabels(false)
            xAxis.setDrawGridLines(false)

            var leftAxis = custom_graph_line_chart.axisLeft
            custom_graph_line_chart.axisRight.isEnabled = false

            leftAxis.setDrawLabels(false)
            leftAxis.setDrawAxisLine(false)
            leftAxis.setDrawGridLines(false)
            leftAxis.setDrawZeroLine(false)

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

    fun setupSpinner(){

        custom_graph_usage_type_spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, v: View?, position: Int, id: Long) {
                isWifiSelected = position != 0
                ischartUpdated = false
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {
                Log.v(
                    "NothingSelected Item",
                    "" + spinner.selectedItem
                )
            }
        }

    }

    fun setupTheme(){
        if(SharedPreferenceDB.getNightMode(this)){
            delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onResume() {
        super.onResume()
        setupTheme()
        ischartUpdated = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.menu_settings){
            startActivity(Intent(this,SettingsActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }


}
