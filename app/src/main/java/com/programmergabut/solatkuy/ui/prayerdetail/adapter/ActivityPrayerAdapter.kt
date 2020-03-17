package com.programmergabut.solatkuy.ui.prayerdetail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.model.prayerApi.Data
import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import kotlinx.android.synthetic.main.layout_prayer_time_detail.view.*

class ActivityPrayerAdapter(private val prayers: MutableList<Data>): RecyclerView.Adapter<ActivityPrayerAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(data: Data){
            itemView.tv_prayer_name.text = data.timings.asr
            itemView.tv_prayer_time.text = data.timings.dhuhr
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_prayer_time_detail, parent, false))

    override fun getItemCount(): Int =
        prayers.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(prayers[position])

    fun addData(api: PrayerApi){
        prayers.addAll(api.data)
    }



}