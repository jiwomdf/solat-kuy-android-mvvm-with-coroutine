package com.programmergabut.solatkuy.ui.main.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.localentity.Dua
import com.programmergabut.solatkuy.databinding.ListDuaBinding
import com.programmergabut.solatkuy.ui.dua.DuaActivity

class DuaCollectionAdapter(private val context: Context): RecyclerView.Adapter<DuaCollectionAdapter.DuaCollectionViewHolder>() {

    private var listData = mutableListOf<Dua>()

    fun setData(datas: List<Dua>){
        listData.clear()
        listData.addAll(datas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuaCollectionViewHolder {
        val binding = DataBindingUtil.inflate<ListDuaBinding>(
            LayoutInflater.from(parent.context), R.layout.list_dua, parent, false
        )
        return DuaCollectionViewHolder(binding)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: DuaCollectionViewHolder, position: Int) = holder.bind(listData[position])

    inner class DuaCollectionViewHolder(private val binding: ListDuaBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: Dua){
            binding.btnDuaBtn.text = data.title
            openDuaAfterAdhan(data)
        }

        private fun openDuaAfterAdhan(data: Dua) {
            val intent = Intent(context, DuaActivity::class.java)
            binding.btnDuaBtn.setOnClickListener {
                intent.apply {
                    putExtra(DuaActivity.DUA_TITLE, data.title)
                    putExtra(DuaActivity.DUA_AR, data.arab)
                    putExtra(DuaActivity.DUA_LT, data.latin)
                    putExtra(DuaActivity.DUA_EN, data.english)
                    putExtra(DuaActivity.DUA_IN, data.indonesia)
                    putExtra(DuaActivity.DUA_REF, data.reference)
                }
                context.startActivity(intent)
            }
        }
    }

}