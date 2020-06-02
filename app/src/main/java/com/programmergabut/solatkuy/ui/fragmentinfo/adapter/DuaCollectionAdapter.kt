package com.programmergabut.solatkuy.ui.fragmentinfo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.localentity.Dua
import com.programmergabut.solatkuy.ui.activityprayer.DuaActivity
import kotlinx.android.synthetic.main.layout_dua_button.view.*

class DuaCollectionAdapter(private val context: Context): RecyclerView.Adapter<DuaCollectionAdapter.DuaCollectionViewHolder>() {

    private var listData = mutableListOf<Dua>()

    fun setData(datas: List<Dua>){
        listData.clear()
        listData.addAll(datas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuaCollectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_dua_button, parent, false)
        return DuaCollectionViewHolder(view)

    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: DuaCollectionViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class DuaCollectionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(data: Dua){
            itemView.btn_duaBtn.text = data.title

            openDua(data)
        }

        /* Dua For After Adhan */
        private fun openDua(data: Dua) {
            val i = Intent(context, DuaActivity::class.java)

            itemView.btn_duaBtn.setOnClickListener {

                i.putExtra(DuaActivity.duaTitle, data.title)
                i.putExtra(DuaActivity.duaAr, data.arab)
                i.putExtra(DuaActivity.duaLt, data.latin)
                i.putExtra(DuaActivity.duaEn, data.english)
                i.putExtra(DuaActivity.duaIn, data.indonesia)
                i.putExtra(DuaActivity.duaRef, data.reference)

                context.startActivity(i)
            }

        }
    }

}