package com.programmergabut.solatkuy.ui.fragmentquran

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.ui.activityreadsurah.ReadSurahActivity
import kotlinx.android.synthetic.main.layout_stared_surah.view.*

class StaredSurahAdapter(private val c: Context) : RecyclerView.Adapter<StaredSurahAdapter.StaredSurahViewHolder>() {

    private var listData = mutableListOf<MsFavSurah>()

    fun setData(datas: List<MsFavSurah>){
        listData.clear()
        listData.addAll(datas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaredSurahViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_stared_surah, parent, false)
        return StaredSurahViewHolder(view)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: StaredSurahViewHolder, position: Int) = holder.bind(listData[position])

    inner class StaredSurahViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bind(data: MsFavSurah){

            itemView.tv_stared_ayah.text = data.surahName
            itemView.cv_stared_ayah.setOnClickListener {

                val i = Intent(c, ReadSurahActivity::class.java)
                i.apply {
                    this.putExtra(ReadSurahActivity.surahID, data.surahID.toString())
                    this.putExtra(ReadSurahActivity.surahName, data.surahName)
                    this.putExtra(ReadSurahActivity.surahTranslation, data.surahTranslation)
                }

                c.startActivities(arrayOf(i))
            }
        }
    }

}