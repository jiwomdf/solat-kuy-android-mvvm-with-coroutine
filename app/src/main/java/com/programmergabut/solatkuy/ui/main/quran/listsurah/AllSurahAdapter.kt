package com.programmergabut.solatkuy.ui.main.quran.listsurah

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.data.local.localentity.MsSurah
import com.programmergabut.solatkuy.databinding.ListAllSurahBinding

class AllSurahAdapter(
    private val onClick: (String, String, String) -> Unit
) : RecyclerView.Adapter<AllSurahAdapter.AllSurahViewHolder>() {

    private val diffCallback = object: DiffUtil.ItemCallback<MsSurah>(){
        override fun areItemsTheSame(oldItem: MsSurah, newItem: MsSurah) = oldItem == newItem
        override fun areContentsTheSame(oldItem: MsSurah, newItem: MsSurah) = oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var listSurah : List<MsSurah>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllSurahViewHolder {
        val binding = ListAllSurahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllSurahViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: AllSurahViewHolder, position: Int) = holder.bind(listSurah[position])

    inner class AllSurahViewHolder(private val binding: ListAllSurahBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MsSurah){
            binding.tvAllsurahNo.text = data.number.toString()
            binding.tvAllsurahEn.text = data.englishName
            binding.tvAllsurahEnMeaning.text = data.englishNameTranslation
            binding.tvAllsurahAr.text = data.name
            binding.ccAllsurah.setOnClickListener {
                onClick(
                    data.number.toString(),
                    data.englishName,
                    data.englishNameTranslation
                )
            }
        }
    }


}