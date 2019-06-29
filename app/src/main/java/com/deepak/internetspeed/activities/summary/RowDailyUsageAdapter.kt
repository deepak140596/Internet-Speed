package com.deepak.internetspeed.activities.summary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deepak.internetspeed.R
import com.deepak.internetspeed.database.DailyConsumption
import com.deepak.internetspeed.util.TrafficUtils
import kotlinx.android.synthetic.main.row_daily_usage.view.*

class RowDailyUsageAdapter(var allUsage: List<DailyConsumption>)
    : RecyclerView.Adapter<RowDailyUsageAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_daily_usage,parent,false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allUsage.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dailyConsumption = allUsage[position]
        holder.bindItems(dailyConsumption)
    }


    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun bindItems(dailyConsumption: DailyConsumption){
            itemView.row_daily_usage_mobile_tv.text = TrafficUtils.getMetricData(dailyConsumption.mobile)
            itemView.row_daily_usage_wifi_tv.text = TrafficUtils.getMetricData(dailyConsumption.wifi)
            itemView.row_daily_usage_total_tv.text = TrafficUtils.getMetricData(dailyConsumption.total)

            itemView.row_daily_usage_date_tv.text = dailyConsumption.timestamp
        }
    }
}