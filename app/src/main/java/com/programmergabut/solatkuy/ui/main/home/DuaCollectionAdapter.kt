package com.programmergabut.solatkuy.ui.main.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.model.Dua
import com.programmergabut.solatkuy.databinding.ListDuaBinding
import com.programmergabut.solatkuy.model.DuaExtraData
import com.programmergabut.solatkuy.ui.dua.DuaActivity

class DuaCollectionAdapter(): RecyclerView.Adapter<DuaCollectionAdapter.DuaCollectionViewHolder>() {

    private var listData = mutableListOf<Dua>()

    fun setData(datas: List<Dua>){
        listData.clear()
        listData.addAll(datas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuaCollectionViewHolder {
        val binding = ListDuaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DuaCollectionViewHolder(binding)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: DuaCollectionViewHolder, position: Int) = holder.bind(listData[position])

    inner class DuaCollectionViewHolder(private val binding: ListDuaBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: Dua){
            binding.btnDuaBtn.text = data.title
            binding.btnDuaBtn.setOnClickListener {
                Intent(itemView.context, DuaActivity::class.java).also {
                    it.putExtra(DuaActivity.DuaData, DuaExtraData(
                        duaTitle = data.title,
                        duaAr = data.arab,
                        duaLt = data.latin,
                        duaEn = data.english,
                        duaIn = data.indonesia,
                        duaRef = data.reference
                    ))
                    itemView.context.startActivity(it)
                }
            }
        }
    }

}