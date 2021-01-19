package com.programmergabut.solatkuy.ui.main.fragmentquran

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data
import com.programmergabut.solatkuy.databinding.ListAllSurahBinding

class AllSurahAdapter(
    private val onClick: (String, String, String) -> Unit
) : RecyclerView.Adapter<AllSurahAdapter.AllSurahViewHolder>() {

    private val diffCallback = object: DiffUtil.ItemCallback<Data>(){
        override fun areItemsTheSame(oldItem: Data, newItem: Data) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Data, newItem: Data) = oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var listData : List<Data>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllSurahViewHolder {
        val binding = DataBindingUtil.inflate<ListAllSurahBinding>(
            LayoutInflater.from(parent.context), R.layout.list_all_surah, parent, false
        )
        return AllSurahViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: AllSurahViewHolder, position: Int) = holder.bind(listData[position])

    inner class AllSurahViewHolder(private val binding: ListAllSurahBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data){
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