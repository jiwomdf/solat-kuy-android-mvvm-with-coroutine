package com.programmergabut.solatkuy.ui.fragmentfavayah

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.databinding.ListFavAyahBinding

class FavAyahAdapter(
    val onClickFavAyah: (MsFavAyah) -> Unit
) : RecyclerView.Adapter<FavAyahAdapter.FavAyahAdapterViewHolder>() {

    private val diffCallback = object: DiffUtil.ItemCallback<MsFavAyah>(){
        override fun areItemsTheSame(oldItem: MsFavAyah, newItem: MsFavAyah) = oldItem == newItem

        override fun areContentsTheSame(oldItem: MsFavAyah, newItem: MsFavAyah) = oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var listAyah : List<MsFavAyah>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavAyahAdapterViewHolder {
        val binding = DataBindingUtil.inflate<ListFavAyahBinding>(
            LayoutInflater.from(parent.context), R.layout.list_fav_ayah, parent, false
        )
        return FavAyahAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int = listAyah.size

    override fun onBindViewHolder(holder: FavAyahAdapterViewHolder, position: Int) = holder.bind(listAyah[position])


    inner class FavAyahAdapterViewHolder(private val binding: ListFavAyahBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MsFavAyah){
            binding.tvListFavAr.text = data.ayahAr
            binding.tvListFavEn.text = data.ayahEn
            binding.tvListFavSurahName.text = data.surahName + " | Ayah " + data.ayahID.toString()

            binding.tvListFavDel.setOnClickListener {
                onClickFavAyah(data)
            }
        }
    }


}