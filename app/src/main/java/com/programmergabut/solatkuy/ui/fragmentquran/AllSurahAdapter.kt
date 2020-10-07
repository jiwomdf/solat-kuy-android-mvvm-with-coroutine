package com.programmergabut.solatkuy.ui.fragmentquran

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data
import com.programmergabut.solatkuy.ui.activityreadsurah.ReadSurahActivity
import kotlinx.android.synthetic.main.layout_all_surah.view.*

class AllSurahAdapter(private val c: Context): RecyclerView.Adapter<AllSurahAdapter.AllSurahViewHolder>() {

    private val diffCallback = object: DiffUtil.ItemCallback<Data>(){
        override fun areItemsTheSame(oldItem: Data, newItem: Data) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Data, newItem: Data) = oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var listData : List<Data>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllSurahViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_all_surah, parent, false)
        return AllSurahViewHolder(view)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: AllSurahViewHolder, position: Int) = holder.bind(listData[position])

    inner class AllSurahViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(data: Data){

            itemView.apply {
                this.tv_allsurah_no.text = data.number.toString()
                this.tv_allsurah_en.text = data.englishName
                this.tv_allsurah_en_meaning.text = data.englishNameTranslation
                this.tv_allsurah_ar.text = data.name
            }

            itemView.cc_allsurah.setOnClickListener {

                val i = Intent(c, ReadSurahActivity::class.java)
                i.apply {
                    this.putExtra(ReadSurahActivity.surahID, data.number.toString())
                    this.putExtra(ReadSurahActivity.surahName, data.englishName)
                    this.putExtra(ReadSurahActivity.surahTranslation, data.englishNameTranslation)
                }

                c.startActivities(arrayOf(i))
            }

        }

    }
}