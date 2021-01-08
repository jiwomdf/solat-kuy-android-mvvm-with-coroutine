package com.programmergabut.solatkuy.ui.fragmentquran

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
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data
import com.programmergabut.solatkuy.databinding.FragmentQuranBinding
import com.programmergabut.solatkuy.util.EnumStatus
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 25/06/20.
 */

@AndroidEntryPoint
class QuranFragment(viewModelTest: QuranFragmentViewModel? = null) : BaseFragment<FragmentQuranBinding, QuranFragmentViewModel>(
    R.layout.fragment_quran, QuranFragmentViewModel::class.java, viewModelTest
), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private lateinit var allSurahAdapter: AllSurahAdapter
    private lateinit var staredSurahAdapter: StaredSurahAdapter
    private var tempAllSurah: List<Data>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRvAllSurah()
        initRvStaredSurah()
        initSearchSurah()
        initJuzzSpinner()
    }

    override fun setListener() {
        super.setListener()
        binding.slQuran.setOnRefreshListener(this)
        binding.cvFavAyah.setOnClickListener(this)
        binding.cvLastReadAyah.setOnClickListener(this)
        observeApi()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.cv_fav_ayah -> {
                findNavController().navigate(QuranFragmentDirections.actionQuranFragmentToFavAyahFragment())
            }
            R.id.cv_last_read_ayah -> {
                if(tempAllSurah == null){
                    showBottomSheet()
                    return
                }
                val selectedSurah = tempAllSurah?.find { surah -> surah.number == getLastReadSurah() }
                if(selectedSurah == null){
                    showBottomSheet(getString(R.string.last_read_ayah_not_found_title), getString(R.string.last_read_ayah_not_found_dsc))
                    return
                }

                findNavController().navigate(
                    QuranFragmentDirections.actionQuranFragmentToReadSurahActivity(
                        selectedSurah.number.toString(),
                        selectedSurah.englishName,
                        selectedSurah.englishNameTranslation,
                        true
                    )
                )
            }
        }
    }

    private fun initSearchSurah() {
        binding.etSearch.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val newData = tempAllSurah?.filter { surah ->
                    surah.englishNameLowerCase!!.contains(s.toString())
                }
                allSurahAdapter.listData = newData ?: emptyList()
                allSurahAdapter.notifyDataSetChanged()
                binding.sJuzz.setSelection(0)

                setVisibility(EnumStatus.SUCCESS, newData)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.sJuzz.setSelection(0, true)
    }

    private fun observeApi(){
        viewModel.allSurah.observe(viewLifecycleOwner, {
            when(it.status){
                EnumStatus.SUCCESS -> {
                    if(it.data == null)
                        showBottomSheet(description = getString(R.string.fetch_failed))

                    tempAllSurah = it.data!!
                    allSurahAdapter.apply {
                        listData = it.data
                        notifyDataSetChanged()
                    }
                    setVisibility(it.status, it.data)
                }
                EnumStatus.LOADING -> setVisibility(it.status, null)
                EnumStatus.ERROR -> setVisibility(it.status, null)
            }
        })

        viewModel.staredSurah.observe(viewLifecycleOwner, {
            staredSurahAdapter.listData = it
            staredSurahAdapter.notifyDataSetChanged()
        })

        viewModel.fetchAllSurah()
    }

    private fun setVisibility(status: EnumStatus, newData: List<Data>?){
        when(status){
            EnumStatus.SUCCESS -> {
                if(newData == null || newData.isEmpty()){
                    binding.tvLoadingAllSurah.visibility = View.VISIBLE
                    binding.tvLoadingAllSurah.text = getString(R.string.text_there_is_no_data)
                }
                else{
                    binding.tvLoadingAllSurah.visibility = View.GONE
                    binding.rvQuranSurah.visibility = View.VISIBLE
                    binding.slQuran.isRefreshing = false
                }
            }
            EnumStatus.LOADING -> {
                binding.tvLoadingAllSurah.text = getString(R.string.loading)
                binding.rvQuranSurah.visibility = View.INVISIBLE
                binding.tvLoadingAllSurah.visibility = View.VISIBLE
            }
            EnumStatus.ERROR -> {
                showBottomSheet(description = getString(R.string.fetch_failed), isCancelable = true, isFinish = false)
                binding.tvLoadingAllSurah.text = getString(R.string.fetch_failed)
                binding.rvQuranSurah.visibility = View.INVISIBLE
                binding.slQuran.isRefreshing = false
            }
        }
    }

    private fun initJuzzSpinner(){
        val arrJuzz = mutableListOf<String>()
        arrJuzz.add("All Juzz")
        for (i in 1..30){
            arrJuzz.add(i.toString())
        }

        binding.sJuzz.adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, arrJuzz)
        binding.sJuzz.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                juzzSurahFilter(binding.sJuzz.selectedItem.toString())
            }
        }
    }

    private fun initRvAllSurah() {
        allSurahAdapter = AllSurahAdapter { number, englishName, englishNameTranslation ->
            findNavController().navigate(
                QuranFragmentDirections.actionQuranFragmentToReadSurahActivity(
                number,
                englishName,
                englishNameTranslation,
                false
            )
            )
        }
        binding.rvQuranSurah.apply {
            adapter = allSurahAdapter
            layoutManager = LinearLayoutManager(this@QuranFragment.context)
        }
    }

    private fun initRvStaredSurah() {
        staredSurahAdapter = StaredSurahAdapter{ surahID, surahName, surahTranslation ->
            findNavController().navigate(QuranFragmentDirections.actionQuranFragmentToReadSurahActivity(
                surahID,
                surahName,
                surahTranslation,
                false
            ))
        }
        binding.rvStaredAyah.apply {
            adapter = staredSurahAdapter
            layoutManager = LinearLayoutManager(this@QuranFragment.context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun juzzSurahFilter(juzz: String){
        val datas: List<Data>
        if(juzz == "All Juzz")
            datas = tempAllSurah ?: emptyList()
        else
            datas = viewModel.getSurahByJuzz(juzz.toInt())

        if(datas.isNotEmpty()){
            allSurahAdapter.listData = datas
            allSurahAdapter.notifyDataSetChanged()
        }
    }

    override fun onRefresh() {
        viewModel.fetchAllSurah()
        binding.sJuzz.setSelection(0)
    }

}