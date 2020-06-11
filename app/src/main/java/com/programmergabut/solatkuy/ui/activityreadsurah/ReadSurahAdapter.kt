package com.programmergabut.solatkuy.ui.activityreadsurah

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah
import kotlinx.android.synthetic.main.layout_read_surah.view.*

class ReadSurahAdapter: RecyclerView.Adapter<ReadSurahAdapter.ReadSurahViewHolder>() {

    private var listAyah = mutableListOf<Ayah>()

    fun setAyah(datas: List<Ayah>){
        listAyah.clear()
        listAyah.addAll(datas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadSurahViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_read_surah, parent, false)
        return ReadSurahViewHolder(view)
    }

    override fun getItemCount(): Int = listAyah.size

    override fun onBindViewHolder(holder: ReadSurahViewHolder, position: Int) = holder.bind(listAyah[position])

    inner class ReadSurahViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(data: Ayah){
            itemView.tv_readSurah_ar.text = data.text
            itemView.tv_readSurah_en.text = data.textEn
            itemView.tv_readSurah_num.text = data.numberInSurah.toString()
        }
    }


}