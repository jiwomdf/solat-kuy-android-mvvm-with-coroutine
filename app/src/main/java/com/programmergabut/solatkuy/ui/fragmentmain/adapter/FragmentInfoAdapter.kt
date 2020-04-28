package com.programmergabut.solatkuy.ui.fragmentmain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.model.asmaalhusnaJson.Data
import kotlinx.android.synthetic.main.layout_ah_viewholder.view.*

class FragmentInfoAdapter(private val datas: List<Data>): RecyclerView.Adapter<FragmentInfoAdapter.FragmentInfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FragmentInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_ah_viewholder, parent, false)
        return FragmentInfoViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: FragmentInfoViewHolder, position: Int) {
        return holder.bind(datas[position])
    }

    inner class FragmentInfoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(data: Data){
            itemView.tv_ah_ar.text = data.name
            itemView.tv_ah_en.text = data.en.meaning
        }
    }
}