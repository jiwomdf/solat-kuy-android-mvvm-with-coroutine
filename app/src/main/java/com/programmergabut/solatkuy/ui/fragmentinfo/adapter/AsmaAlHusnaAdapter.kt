package com.programmergabut.solatkuy.ui.fragmentinfo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.Data
import kotlinx.android.synthetic.main.layout_ah_viewholder.view.*

class AsmaAlHusnaAdapter(private val datas: List<Data>): RecyclerView.Adapter<AsmaAlHusnaAdapter.AsmaAlHusnaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsmaAlHusnaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_ah_viewholder, parent, false)
        return AsmaAlHusnaViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: AsmaAlHusnaViewHolder, position: Int) {
        return holder.bind(datas[position])
    }

    inner class AsmaAlHusnaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(data: Data){
            itemView.tv_ah_ar.text = data.name
            itemView.tv_ah_en.text = data.en.meaning
            itemView.tv_ah_num.text = data.number.toString()
        }
    }
}