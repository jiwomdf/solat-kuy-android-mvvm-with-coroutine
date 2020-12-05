package com.programmergabut.solatkuy.ui.activityreadsurah

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah
import kotlinx.android.synthetic.main.layout_read_surah.view.*

class ReadSurahAdapter(
    val onClick: (Ayah, View) -> Unit,
    val setTheme: (View) -> Unit,
    val isFav: Drawable,
    val isNotFav: Drawable,
    val accentColor: Int
) : RecyclerView.Adapter<ReadSurahAdapter.ReadSurahViewHolder>() {

    private val diffCallback = object: DiffUtil.ItemCallback<Ayah>(){
        override fun areItemsTheSame(oldItem: Ayah, newItem: Ayah) = oldItem == newItem

        override fun areContentsTheSame(oldItem: Ayah, newItem: Ayah) = oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var listAyah : List<Ayah>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadSurahViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_read_surah, parent, false)

        return ReadSurahViewHolder(view)
    }

    override fun getItemCount(): Int = listAyah.size

    override fun onBindViewHolder(holder: ReadSurahViewHolder, position: Int) = holder.bind(listAyah[position])

    inner class ReadSurahViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(data: Ayah){
            itemView.tv_listFav_ar.text = data.text
            itemView.tv_listFav_en.text = data.textEn
            itemView.tv_listFav_num.text = data.numberInSurah.toString()

            setTheme(itemView)

            if(data.isFav)
                itemView.iv_listFav_fav.setImageDrawable(isFav)
            else
                itemView.iv_listFav_fav.setImageDrawable(isNotFav)

            if(data.isLastRead){
                itemView.cl_vh_readSurah.setBackgroundColor(accentColor)
                /* itemView.tv_listFav_ar.setTextColor(whiteColor)
                itemView.tv_listFav_en.setTextColor(whiteColor)
                itemView.tv_listFav_num.setTextColor(whiteColor) */
            }

            itemView.iv_listFav_fav.setOnClickListener {
                onClick(data, itemView)
            }

        }
    }




}