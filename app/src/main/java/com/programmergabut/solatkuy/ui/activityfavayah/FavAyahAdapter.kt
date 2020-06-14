package com.programmergabut.solatkuy.ui.activityfavayah

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import kotlinx.android.synthetic.main.layout_read_surah.view.*

class FavAyahAdapter : RecyclerView.Adapter<FavAyahAdapter.FavAyahAdapterViewHolder>() {

    private var listAyah = mutableListOf<MsFavAyah>()

    fun setAyah(datas: List<MsFavAyah>){
        listAyah.clear()
        listAyah.addAll(datas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavAyahAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_read_surah, parent, false)
        return FavAyahAdapterViewHolder(view)
    }

    override fun getItemCount(): Int = listAyah.size

    override fun onBindViewHolder(holder: FavAyahAdapterViewHolder, position: Int) = holder.bind(listAyah[position])


    inner class FavAyahAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(data: MsFavAyah){
            itemView.tv_readSurah_ar.text = data.ayahAr
            itemView.tv_readSurah_en.text = data.ayahEn
            itemView.tv_readSurah_num.text = ""

            /* if(data.isFav)
                itemView.tv_readSurah_fav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_red_24))
            else
                itemView.tv_readSurah_fav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_24)) */

            /* itemView.tv_readSurah_fav.setOnClickListener {

                  val msFavAyah = MsFavAyah(surahId.toInt(), data.numberInSurah, data.text, data.textEn!!)

                 if(itemView.tv_readSurah_fav.drawable.constantState == context.getDrawable(R.drawable.ic_favorite_red_24)?.constantState){
                     viewModel.selectedSurahAr.postValue(Resource.loading(null))
                     Toasty.info(context, "Unsaving the ayah", Toast.LENGTH_SHORT).show()

                     viewModel.deleteFavAyah(msFavAyah)
                     itemView.tv_readSurah_fav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_24))
                 }
                 else{
                     //Toasty.info(context, "Saving the ayah", Toast.LENGTH_SHORT).show()
                     viewModel.insertFavAyah(msFavAyah)
                     itemView.tv_readSurah_fav.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_red_24))
                 }

                 viewModel.fetchQuranSurah(surahId.toInt())
             } */
        }
    }


}