package com.programmergabut.solatkuy.ui.main.quran.readsurah

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.localentity.MsAyah
import com.programmergabut.solatkuy.databinding.ListReadSurahBinding

class ReadSurahAdapter(
    val setTheme: (ListReadSurahBinding) -> Unit,
    val setContent: (ListReadSurahBinding) -> Unit,
    val textSize: (ListReadSurahBinding) -> Unit,
    val isFav: Drawable,
    val accentColor: Int
) : RecyclerView.Adapter<ReadSurahAdapter.ReadSurahViewHolder>() {

    private val diffCallback = object: DiffUtil.ItemCallback<MsAyah>(){
        override fun areItemsTheSame(oldItem: MsAyah, newItem: MsAyah) = oldItem == newItem
        override fun areContentsTheSame(oldItem: MsAyah, newItem: MsAyah) = oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var listAyah : List<MsAyah>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadSurahViewHolder {
        val binding = ListReadSurahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReadSurahViewHolder(binding)
    }

    override fun getItemCount(): Int = listAyah.size

    override fun onBindViewHolder(holder: ReadSurahViewHolder, position: Int) = holder.bind(listAyah[position])

    inner class ReadSurahViewHolder(private val binding: ListReadSurahBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MsAyah){
            binding.apply {
                tvListFavAr.text = data.text
                tvListFavEn.text = data.textEn
                tvListFavNum.text = data.numberInSurah.toString()
                setTheme(this)
                setContent(this)
                textSize(this)

                if(data.isLastRead){
                    clVhReadSurah.setBackgroundColor(accentColor)
                }

            }
        }
    }

}