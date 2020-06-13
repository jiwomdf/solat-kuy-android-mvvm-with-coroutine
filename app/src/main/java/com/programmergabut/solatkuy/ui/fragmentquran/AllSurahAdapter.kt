package com.programmergabut.solatkuy.ui.fragmentquran

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data
import com.programmergabut.solatkuy.ui.activityreadsurah.ReadSurahActivity
import kotlinx.android.synthetic.main.layout_all_surah.view.*

class AllSurahAdapter(private val c: Context): RecyclerView.Adapter<AllSurahAdapter.AllSurahViewHolder>() {

    private var listData = mutableListOf<Data>()

    fun setData(datas: List<Data>){
        listData.clear()
        listData.addAll(datas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllSurahViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_all_surah, parent, false)
        return AllSurahViewHolder(view)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: AllSurahViewHolder, position: Int) = holder.bind(listData[position])

    inner class AllSurahViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(data: Data){

            val i = Intent(c, ReadSurahActivity::class.java)
            i.apply {
                this.putExtra(ReadSurahActivity.surahID, data.number.toString())
                this.putExtra(ReadSurahActivity.surahName, data.englishName)
                this.putExtra(ReadSurahActivity.surahTranslation, data.englishNameTranslation)
            }

            itemView.apply {
                this.tv_allsurah_no.text = data.number.toString()
                this.tv_allsurah_en.text = data.englishName
                this.tv_allsurah_en_meaning.text = data.englishNameTranslation
                this.tv_allsurah_ar.text = data.name
            }

            itemView.cc_allsurah.setOnClickListener {
                c.startActivities(arrayOf(i))
            }

        }

    }
}