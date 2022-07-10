package com.programmergabut.solatkuy.quran.quran.listsurah

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.data.local.localentity.MsFavSurah
import com.programmergabut.solatkuy.databinding.ListStaredSurahBinding

class StaredSurahAdapter : RecyclerView.Adapter<StaredSurahAdapter.StaredSurahViewHolder>() {

    private val diffCallback = object: DiffUtil.ItemCallback<MsFavSurah>(){
        override fun areItemsTheSame(oldItem: MsFavSurah, newItem: MsFavSurah) = oldItem == newItem
        override fun areContentsTheSame(oldItem: MsFavSurah, newItem: MsFavSurah) = oldItem == newItem
    }

    var onClick: ((surahID: String, surahName: String, surahTranslation: String ) -> Unit)? = null
    private val differ = AsyncListDiffer(this, diffCallback)

    var listData : List<MsFavSurah>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaredSurahViewHolder {
        val binding = ListStaredSurahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StaredSurahViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: StaredSurahViewHolder, position: Int) = holder.bind(listData[position])

    inner class StaredSurahViewHolder(private val binding: ListStaredSurahBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: MsFavSurah){
            binding.apply {
                tvStaredAyah.text = data.surahName
                cvStaredAyah.setOnClickListener {
                    onClick?.invoke(
                        data.surahID.toString(),
                        data.surahName!!,
                        data.surahTranslation!!
                    )
                }
            }
        }
    }

}