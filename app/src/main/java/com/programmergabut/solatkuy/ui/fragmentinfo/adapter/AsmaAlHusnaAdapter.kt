package com.programmergabut.solatkuy.ui.fragmentinfo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.Data
import com.programmergabut.solatkuy.databinding.ListAsmaAlhusnaBinding

class AsmaAlHusnaAdapter(private val datas: List<Data>): RecyclerView.Adapter<AsmaAlHusnaAdapter.AsmaAlHusnaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsmaAlHusnaViewHolder {
        val binding = DataBindingUtil.inflate<ListAsmaAlhusnaBinding>(
            LayoutInflater.from(parent.context), R.layout.list_asma_alhusna, parent, true
        )
        return AsmaAlHusnaViewHolder(binding)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: AsmaAlHusnaViewHolder, position: Int) {
        return holder.bind(datas[position])
    }

    inner class AsmaAlHusnaViewHolder(private val binding: ListAsmaAlhusnaBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data){
            binding.tvAhAr.text = data.name
            binding.tvAhEn.text = data.en.meaning
            binding.tvAhNum.text = data.number.toString()
        }
    }
}