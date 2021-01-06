package com.programmergabut.solatkuy.ui.fragmentmain.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.localentity.Dua
import com.programmergabut.solatkuy.databinding.ListDuaBinding
import com.programmergabut.solatkuy.ui.DuaActivity

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

    override fun onBindViewHolder(holder: DuaCollectionViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class DuaCollectionViewHolder(private val binding: ListDuaBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: Dua){
            binding.btnDuaBtn.text = data.title

            openDua(data)
        }

        /* Dua For After Adhan */
        private fun openDua(data: Dua) {
            val i = Intent(context, DuaActivity::class.java)

            binding.btnDuaBtn.setOnClickListener {

                i.putExtra(DuaActivity.DUA_TITLE, data.title)
                i.putExtra(DuaActivity.DUA_AR, data.arab)
                i.putExtra(DuaActivity.DUA_LT, data.latin)
                i.putExtra(DuaActivity.DUA_EN, data.english)
                i.putExtra(DuaActivity.DUA_IN, data.indonesia)
                i.putExtra(DuaActivity.DUA_REF, data.reference)

                context.startActivity(i)
            }

        }
    }

}