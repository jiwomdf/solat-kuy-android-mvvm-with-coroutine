package com.programmergabut.solatkuy.ui.activityreadsurah

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah
import com.programmergabut.solatkuy.databinding.ListReadSurahBinding

class ReadSurahAdapter(
    val onClickFavAyah: (Ayah, ListReadSurahBinding) -> Unit,
    val setTheme: (ListReadSurahBinding) -> Unit,
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
        val binding = DataBindingUtil.inflate<ListReadSurahBinding>(
            LayoutInflater.from(parent.context),
            R.layout.list_read_surah, parent, false
        )

        return ReadSurahViewHolder(binding)
    }

    override fun getItemCount(): Int = listAyah.size

    override fun onBindViewHolder(holder: ReadSurahViewHolder, position: Int) = holder.bind(listAyah[position])

    inner class ReadSurahViewHolder(private val binding: ListReadSurahBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Ayah){

            binding.tvListFavAr.text = data.text
            binding.tvListFavEn.text = data.textEn
            binding.tvListFavNum.text = data.numberInSurah.toString()

            setTheme(binding)

            if(data.isFav)
                binding.ivListFavFav.setImageDrawable(isFav)
            else
                binding.ivListFavFav.setImageDrawable(isNotFav)

            if(data.isLastRead){
                binding.clVhReadSurah.setBackgroundColor(accentColor)
                /* binding.tv_listFav_ar.setTextColor(whiteColor)
                binding.tv_listFav_en.setTextColor(whiteColor)
                binding.tv_listFav_num.setTextColor(whiteColor) */
            }

            binding.ivListFavFav.setOnClickListener {
                onClickFavAyah(data, binding)
            }

        }
    }




}