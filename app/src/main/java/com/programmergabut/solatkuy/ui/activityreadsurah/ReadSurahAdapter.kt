package com.programmergabut.solatkuy.ui.activityreadsurah

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah
import kotlinx.android.synthetic.main.layout_read_surah.view.*

class ReadSurahAdapter(private val context: Context, private val viewModel: ReadSurahViewModel,
                       private val surahId: String)
    : RecyclerView.Adapter<ReadSurahAdapter.ReadSurahViewHolder>() {

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

            if(data.isFav)
                itemView.tv_readSurah_fav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_red_24))
            else
                itemView.tv_readSurah_fav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_24))

            itemView.cl_readSurah.setOnClickListener {

                //val bottomSheet = BottomSheetDialog(context)

                //viewModel.insertFavAyah(MsFavAyah(surahId.toInt(), data.numberInSurah, data.text, data.textEn!!))
            }
        }
    }


}