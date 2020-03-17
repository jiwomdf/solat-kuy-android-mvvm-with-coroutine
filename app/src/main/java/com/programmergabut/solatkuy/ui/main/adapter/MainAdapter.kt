package com.programmergabut.solatkuy.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.model.prayerApi.Data
import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import kotlinx.android.synthetic.main.mainlayout.view.*

class MainAdapter(private val prayers: MutableList<Data>): RecyclerView.Adapter<MainAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(data: Data){
            itemView.tvTitle.text = data.timings.asr
            itemView.tvTime.text = data.timings.dhuhr
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.mainlayout, parent, false))

    override fun getItemCount(): Int =
        prayers.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(prayers[position])

    fun addData(api: PrayerApi){
        prayers.addAll(api.data)
    }



}