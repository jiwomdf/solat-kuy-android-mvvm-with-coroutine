package com.programmergabut.solatkuy.ui.main.quran.listsurah

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseFragment
import com.programmergabut.solatkuy.data.local.localentity.MsSurah
import com.programmergabut.solatkuy.databinding.FragmentListSurahBinding
import com.programmergabut.solatkuy.util.Status
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 25/06/20.
 */
@AndroidEntryPoint
class ListSurahFragment(
    viewModelTest: ListSurahViewModel? = null
) : BaseFragment<FragmentListSurahBinding, ListSurahViewModel>(
    R.layout.fragment_list_surah,
    ListSurahViewModel::class.java,
    viewModelTest
), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private val ALL_JUZZ = "All Juzz"
    private lateinit var allSurahAdapter: AllSurahAdapter
    private lateinit var staredSurahAdapter: StaredSurahAdapter

    override fun getViewBinding() = FragmentListSurahBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRvAllSurah()
        initRvStaredSurah()
        initJuzzSpinner()
        setListener()
        viewModel.getAllSurah()
    }

    private fun setListener() {
        binding.apply {
            slQuran.setOnRefreshListener(this@ListSurahFragment)
            cvLastReadAyah.setOnClickListener(this@ListSurahFragment)
            etSearch.addTextChangedListener(etSearchListener)

            viewModel.allSurah.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.Success, Status.Error -> {
                        if (it.data == null) {
                            setVisibility(it.status, null)
                            return@observe
                        }
                        updateListSurahAdapter(it.data)
                        setVisibility(it.status, it.data)
                    }
                    Status.Loading -> {
                        setVisibility(it.status, null)
                    }
                }
            }

            viewModel.staredSurah.observe(viewLifecycleOwner) {
                staredSurahAdapter.listData = it
                staredSurahAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.cv_last_read_ayah -> {
                viewModel.allSurah.value?.data?.let {
                    val selectedSurah = getLastReadSurah(it, sharedPrefUtil.getLastReadSurah())
                    if(selectedSurah == null){
                        showBottomSheet(
                            getString(R.string.last_read_ayah_not_found_title),
                            getString(R.string.last_read_ayah_not_found_dsc)
                        )
                        return
                    }
                    resetSpinnerAndSearchBarValue()
                    findNavController().navigate(
                        ListSurahFragmentDirections.actionQuranFragmentToReadSurahActivity(
                            selectedSurah.number.toString(),
                            selectedSurah.englishName,
                            selectedSurah.englishNameTranslation,
                            true
                        )
                    )
                }
            }
        }
    }

    private fun getLastReadSurah(allSurah: List<MsSurah>, lastSurah: Int): MsSurah? {
        allSurah.let {
            return it.find { surah ->
                surah.number == lastSurah
            }
        }
    }

    private val etSearchListener = object: TextWatcher{
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(s.isNullOrEmpty() && binding.sJuzz.selectedItemPosition != 0)
                return
            val list = viewModel.searchSurah(s.toString())
            allSurahAdapter.listSurah = list
            allSurahAdapter.notifyDataSetChanged()
            binding.sJuzz.setSelection(0, true)
        }
    }

    private fun updateListSurahAdapter(datas: List<MsSurah>){
        allSurahAdapter.listSurah = datas
        allSurahAdapter.notifyDataSetChanged()
    }

    private fun setVisibility(status: Status, data: List<MsSurah>?){
        binding.apply {
            when(status){
                Status.Success, Status.Error -> {
                    if(data == null || data.isEmpty()){
                        tvLoadingAllSurah.visibility = View.VISIBLE
                        tvLoadingAllSurah.text = getString(R.string.text_there_is_no_data)
                    } else {
                        tvLoadingAllSurah.visibility = View.GONE
                        rvQuranSurah.visibility = View.VISIBLE
                    }
                    slQuran.isRefreshing = false
                }
                Status.Loading -> {
                    tvLoadingAllSurah.visibility = View.VISIBLE
                    tvLoadingAllSurah.text = getString(R.string.loading)
                    rvQuranSurah.visibility = View.GONE
                }
            }
        }
    }

    private fun initJuzzSpinner() {
        binding.apply {
            val arrJuzz = mutableListOf<String>()
            arrJuzz.add(ALL_JUZZ)
            for (i in 1..30) {
                arrJuzz.add(i.toString())
            }
            sJuzz.adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, arrJuzz)
            sJuzz.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position == 0 && etSearch.text.toString() != "")
                        return
                    if(sJuzz.selectedItem.toString() == ALL_JUZZ){
                        updateListSurahAdapter(viewModel.getSurahByJuzz(0))
                    } else{
                        updateListSurahAdapter(viewModel.getSurahByJuzz(sJuzz.selectedItem.toString().toInt()))
                    }
                    binding.etSearch.setText("")
                }
            }
        }
    }

    private fun initRvAllSurah() {
        allSurahAdapter = AllSurahAdapter()
        binding.rvQuranSurah.apply {
            adapter = allSurahAdapter
            layoutManager = LinearLayoutManager(this@ListSurahFragment.context)
        }
        allSurahAdapter.onClick = { number, englishName, englishNameTranslation ->
            resetSpinnerAndSearchBarValue()
            findNavController().navigate(ListSurahFragmentDirections
                .actionQuranFragmentToReadSurahActivity(
                    number,
                    englishName,
                    englishNameTranslation,
                    false
            ))
        }
    }

    private fun initRvStaredSurah() {
        staredSurahAdapter = StaredSurahAdapter()
        binding.rvStaredAyah.apply {
            adapter = staredSurahAdapter
            layoutManager = LinearLayoutManager(this@ListSurahFragment.context, LinearLayoutManager.HORIZONTAL, false)
        }
        staredSurahAdapter.onClick = { surahID, surahName, surahTranslation ->
            resetSpinnerAndSearchBarValue()
            findNavController().navigate(ListSurahFragmentDirections.actionQuranFragmentToReadSurahActivity(
                surahID,
                surahName,
                surahTranslation,
                false
            ))
        }
    }



    private fun resetSpinnerAndSearchBarValue(){
        binding.apply {
            sJuzz.onItemSelectedListener = null
            etSearch.removeTextChangedListener(etSearchListener)
            etSearch.setText("")
            sJuzz.setSelection(0)
        }
    }

    override fun onRefresh() {
        binding.apply {
            viewModel.getAllSurah()
            sJuzz.setSelection(0)
            etSearch.setText("")
        }
    }

}