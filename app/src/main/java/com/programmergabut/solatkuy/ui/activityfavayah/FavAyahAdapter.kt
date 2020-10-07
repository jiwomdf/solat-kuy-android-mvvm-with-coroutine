package com.programmergabut.solatkuy.ui.activityfavayah

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import kotlinx.android.synthetic.main.layout_delete.view.*
import kotlinx.android.synthetic.main.layout_listfav_surah.view.*

class FavAyahAdapter(private val context: Context,
                     private val favAyahViewModel: FavAyahViewModel)
    : RecyclerView.Adapter<FavAyahAdapter.FavAyahAdapterViewHolder>() {

    private val diffCallback = object: DiffUtil.ItemCallback<MsFavAyah>(){
        override fun areItemsTheSame(oldItem: MsFavAyah, newItem: MsFavAyah) = oldItem == newItem

        override fun areContentsTheSame(oldItem: MsFavAyah, newItem: MsFavAyah) = oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var listAyah : List<MsFavAyah>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavAyahAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_listfav_surah, parent, false)
        return FavAyahAdapterViewHolder(view)
    }

    override fun getItemCount(): Int = listAyah.size

    override fun onBindViewHolder(holder: FavAyahAdapterViewHolder, position: Int) = holder.bind(listAyah[position])


    inner class FavAyahAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(data: MsFavAyah){
            itemView.tv_listFav_ar.text = data.ayahAr
            itemView.tv_listFav_en.text = data.ayahEn
            itemView.tv_listFav_surahName.text = data.surahName + " | Ayah " + data.ayahID.toString()

            itemView.tv_listFav_del.setOnClickListener {

                val dialog = Dialog(context)
                val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_delete, null)
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                dialog.setContentView(dialogView)
                dialog.show()

                dialogView.btn_layoutDel_Delete.setOnClickListener {
                    favAyahViewModel.deleteFavAyah(data)
                    dialog.hide()
                }

                dialogView.btn_layoutDel_Cancel.setOnClickListener { dialog.hide() }

            }

        }
    }


}