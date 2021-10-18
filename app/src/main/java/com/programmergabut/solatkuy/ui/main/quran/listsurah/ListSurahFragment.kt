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
import com.programmergabut.solatkuy.util.EnumStatus
import java.util.*


class ListSurahFragment(
    viewModelTest: ListSurahViewModel? = null
) : BaseFragment<FragmentListSurahBinding, ListSurahViewModel>(
    R.layout.fragment_list_surah,
    ListSurahViewModel::class,
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
        viewModel.getAllSurah()
    }

    override fun setListener() {
        super.setListener()
        binding.slQuran.setOnRefreshListener(this)
        binding.cvLastReadAyah.setOnClickListener(this)
        binding.etSearch.addTextChangedListener(etSearchListener)

        viewModel.allSurah.observe(viewLifecycleOwner, {
            when (it.status) {
                EnumStatus.SUCCESS, EnumStatus.ERROR -> {
                    if (it.data == null) {
                        setVisibility(it.status, null)
                        return@observe
                    }
                    updateListSurahAdapter(it.data)
                    setVisibility(it.status, it.data)
                }
                EnumStatus.LOADING -> {
                    setVisibility(it.status, null)
                }
            }
        })

        viewModel.staredSurah.observe(viewLifecycleOwner, {
            staredSurahAdapter.listData = it
            staredSurahAdapter.notifyDataSetChanged()
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cv_last_read_ayah -> {
                viewModel.allSurah.value?.data?.let {
                    val selectedSurah = getLastReadSurah(it, viewModel.sharedPrefUtil.getLastReadSurah())
                    if (selectedSurah == null) {
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

    private val etSearchListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.isNullOrEmpty() && binding.sJuzz.selectedItemPosition != 0)
                return
            val list = getSurahBySeachString(s.toString())
            allSurahAdapter.listSurah = list
            allSurahAdapter.notifyDataSetChanged()
            binding.sJuzz.setSelection(0, true)
        }
    }

    fun getSurahBySeachString(stringName: String): List<MsSurah> {
        viewModel.allSurah?.value?.data?.let {
            val lowerCaseString =
                if (stringName.isNotEmpty()) stringName.lowercase(Locale.ROOT).trim() else ""
            val list = it.filter { surah ->
                surah.englishNameLowerCase!!.contains(lowerCaseString)
            }
            return list
        } ?: run {
            return emptyList()
        }
    }

    private fun updateListSurahAdapter(datas: List<MsSurah>) {
        allSurahAdapter.listSurah = datas
        allSurahAdapter.notifyDataSetChanged()
    }

    private fun setVisibility(status: EnumStatus, data: List<MsSurah>?) {
        when (status) {
            EnumStatus.SUCCESS, EnumStatus.ERROR -> {
                if (data == null || data.isEmpty()) {
                    binding.tvLoadingAllSurah.visibility = View.VISIBLE
                    binding.tvLoadingAllSurah.text = getString(R.string.text_there_is_no_data)
                } else {
                    binding.tvLoadingAllSurah.visibility = View.GONE
                    binding.rvQuranSurah.visibility = View.VISIBLE
                }
                binding.slQuran.isRefreshing = false
            }
            EnumStatus.LOADING -> {
                binding.tvLoadingAllSurah.visibility = View.VISIBLE
                binding.tvLoadingAllSurah.text = getString(R.string.loading)
                binding.rvQuranSurah.visibility = View.GONE
            }
        }
    }

    private fun initJuzzSpinner() {
        val arrJuzz = mutableListOf<String>()
        arrJuzz.add(ALL_JUZZ)
        for (i in 1..30) {
            arrJuzz.add(i.toString())
        }
        binding.sJuzz.adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, arrJuzz)
        binding.sJuzz.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0 && binding.etSearch.text.toString() != "")
                    return
                juzzSurahFilter(binding.sJuzz.selectedItem.toString())
                binding.etSearch.setText("")
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
            findNavController().navigate(
                ListSurahFragmentDirections
                    .actionQuranFragmentToReadSurahActivity(
                        number,
                        englishName,
                        englishNameTranslation,
                        false
                    )
            )
        }
    }

    private fun initRvStaredSurah() {
        staredSurahAdapter = StaredSurahAdapter()
        binding.rvStaredAyah.apply {
            adapter = staredSurahAdapter
            layoutManager = LinearLayoutManager(
                this@ListSurahFragment.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
        staredSurahAdapter.onClick = { surahID, surahName, surahTranslation ->
            resetSpinnerAndSearchBarValue()
            findNavController().navigate(
                ListSurahFragmentDirections.actionQuranFragmentToReadSurahActivity(
                    surahID,
                    surahName,
                    surahTranslation,
                    false
                )
            )
        }
    }

    private fun juzzSurahFilter(juzz: String) {
        if (juzz == ALL_JUZZ) {
            updateListSurahAdapter(getSurahByJuzz(0))
        } else {
            updateListSurahAdapter(getSurahByJuzz(juzz.toInt()))
        }
    }

    private fun getSurahByJuzz(juzz: Int): List<MsSurah> {
        viewModel.allSurah.value?.data?.let { allSurah ->
            return when (juzz) {
                0 -> allSurah
                1 -> allSurah.filter { x -> x.number in 1..2 }
                2 -> allSurah.filter { x -> x.number == 2 }
                3 -> allSurah.filter { x -> x.number in 2..3 }
                4 -> allSurah.filter { x -> x.number in 3..4 }
                5 -> allSurah.filter { x -> x.number == 4 }
                6 -> allSurah.filter { x -> x.number in 4..5 }
                7 -> allSurah.filter { x -> x.number in 5..6 }
                8 -> allSurah.filter { x -> x.number in 6..7 }
                9 -> allSurah.filter { x -> x.number in 7..8 }
                10 -> allSurah.filter { x -> x.number in 8..9 }
                11 -> allSurah.filter { x -> x.number in 9..11 }
                12 -> allSurah.filter { x -> x.number in 11..12 }
                13 -> allSurah.filter { x -> x.number in 12..14 }
                14 -> allSurah.filter { x -> x.number in 15..16 }
                15 -> allSurah.filter { x -> x.number in 17..18 }
                16 -> allSurah.filter { x -> x.number in 18..20 }
                17 -> allSurah.filter { x -> x.number in 21..22 }
                18 -> allSurah.filter { x -> x.number in 23..25 }
                19 -> allSurah.filter { x -> x.number in 25..27 }
                20 -> allSurah.filter { x -> x.number in 27..29 }
                21 -> allSurah.filter { x -> x.number in 29..33 }
                22 -> allSurah.filter { x -> x.number in 33..36 }
                23 -> allSurah.filter { x -> x.number in 36..38 }
                24 -> allSurah.filter { x -> x.number in 39..41 }
                25 -> allSurah.filter { x -> x.number in 41..45 }
                26 -> allSurah.filter { x -> x.number in 46..51 }
                27 -> allSurah.filter { x -> x.number in 51..57 }
                28 -> allSurah.filter { x -> x.number in 58..66 }
                29 -> allSurah.filter { x -> x.number in 67..77 }
                30 -> allSurah.filter { x -> x.number in 78..144 }
                else -> allSurah
            }
        } ?: return emptyList()
    }

    private fun resetSpinnerAndSearchBarValue() {
        binding.sJuzz.onItemSelectedListener = null
        binding.etSearch.removeTextChangedListener(etSearchListener)
        binding.etSearch.setText("")
        binding.sJuzz.setSelection(0)
    }

    override fun onRefresh() {
        viewModel.getAllSurah()
        binding.sJuzz.setSelection(0)
        binding.etSearch.setText("")
    }

}