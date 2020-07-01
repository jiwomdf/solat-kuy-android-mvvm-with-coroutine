package com.programmergabut.solatkuy.ui.activityreadsurah

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.layout_read_surah.view.*

class ReadSurahAdapter(private val context: Context, private val viewModel: ReadSurahViewModel,
                       private val surahId: String, private val surahName: String)
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
            itemView.tv_listFav_ar.text = data.text
            itemView.tv_listFav_en.text = data.textEn
            itemView.tv_listFav_num.text = data.numberInSurah.toString()

            if(data.isFav)
                itemView.iv_listFav_fav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_red_24))
            else
                itemView.iv_listFav_fav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_24))

            itemView.iv_listFav_fav.setOnClickListener {

                val msFavAyah = MsFavAyah(surahId.toInt(), data.numberInSurah, surahName, data.text, data.textEn!!)

                if(itemView.iv_listFav_fav.drawable.constantState == context.getDrawable(R.drawable.ic_favorite_red_24)?.constantState){
                    Toasty.info(context, "Unsaving the ayah", Toast.LENGTH_SHORT).show()

                    viewModel.deleteFavAyah(msFavAyah)
                    itemView.iv_listFav_fav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_24))
                }
                else{
                    Toasty.info(context, "Saving the ayah", Toast.LENGTH_SHORT).show()

                    viewModel.insertFavAyah(msFavAyah)
                    itemView.iv_listFav_fav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_red_24))
                }

                viewModel.fetchQuranSurah(surahId.toInt())
            }
        }
    }


}