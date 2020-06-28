package com.programmergabut.solatkuy.ui.fragmentquran

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data
import com.programmergabut.solatkuy.ui.activityfavayah.FavAyahActivity
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
import com.programmergabut.solatkuy.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_quran.*
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 25/06/20.
 */

//@AndroidEntryPoint
class QuranFragment : Fragment(R.layout.fragment_quran), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var fragmentQuranFragmentViewModel: QuranFragmentViewModel
    //private val fragmentQuranFragmentViewModel: QuranFragmentViewModel by viewModels()

    private lateinit var allSurahAdapter: AllSurahAdapter
    private lateinit var staredSurahAdapter: StaredSurahAdapter
    private var allSurahDatas: MutableList<Data>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentQuranFragmentViewModel = ViewModelProvider(this, ViewModelFactory
            .getInstance(activity?.application!!, requireContext()))[QuranFragmentViewModel::class.java]

        initRvAllSurah()
        initRvStaredSurah()
        observeApi()
        refreshLayout()
        searchSurah()
        createJuzzSpinner()

        cv_fav_ayah.setOnClickListener {
            val i = Intent(context, FavAyahActivity::class.java)

            context?.startActivities(arrayOf(i))
        }
    }

    private fun searchSurah() {
        et_search.addTextChangedListener(object: TextWatcher{

            override fun afterTextChanged(s: Editable?) {
                val newData = allSurahDatas!!.filter { x -> x.englishNameLC!!.contains(s.toString()) }

                allSurahAdapter.setData(newData)
                allSurahAdapter.notifyDataSetChanged()

                s_juzz.setSelection(0)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        s_juzz.setSelection(0, true)
    }

    private fun observeApi(){
        fragmentQuranFragmentViewModel.allSurah.observe(viewLifecycleOwner, Observer {
            when(it.status){
                EnumStatus.SUCCESS -> {
                    val datas = it.data?.data!! as MutableList<Data>
                    allSurahAdapter.setData(datas)
                    allSurahAdapter.notifyDataSetChanged()

                    allSurahDatas = datas.map { x ->
                        Data(
                            x.englishName,
                            x.englishName.toLowerCase(Locale.getDefault()).replace("-", " "),
                            x.englishNameTranslation,
                            x.name,
                            x.number,
                            x.numberOfAyahs,
                            x.revelationType
                        )
                    } as MutableList<Data>

                    tv_loading_all_surah.visibility = View.GONE
                    rv_quran_surah.visibility = View.VISIBLE
                }
                EnumStatus.LOADING -> {
                    rv_quran_surah.visibility = View.INVISIBLE
                    tv_loading_all_surah.visibility = View.VISIBLE
                    tv_loading_all_surah.text = getString(R.string.loading)
                }
                EnumStatus.ERROR -> {
                    rv_quran_surah.visibility = View.INVISIBLE
                    tv_loading_all_surah.text = getString(R.string.fetch_failed)
                }
            }
        })

        fragmentQuranFragmentViewModel.staredSurah.observe(viewLifecycleOwner, Observer {
            when(it.status){
                EnumStatus.SUCCESS -> {
                    staredSurahAdapter.setData(it.data!!)
                    staredSurahAdapter.notifyDataSetChanged()
                }
                EnumStatus.LOADING -> {}
                EnumStatus.ERROR -> {}
            }

        })

        fetchAllSurah()
        fragmentQuranFragmentViewModel.getStaredSurah()
    }

    private fun createJuzzSpinner(){
        val arrJuzz = mutableListOf<String>()
        arrJuzz.add("All Juzz")
        for (i in 1..30){ arrJuzz.add(i.toString()) }

        s_juzz.adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, arrJuzz)

        s_juzz.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                juzzSurahFilter(s_juzz.selectedItem.toString())
            }
        }

    }

    private fun initRvAllSurah() {
        allSurahAdapter = AllSurahAdapter(requireContext())

        rv_quran_surah.apply {
            adapter = allSurahAdapter
            layoutManager = LinearLayoutManager(this@QuranFragment.context)
            setHasFixedSize(true)
        }
    }

    private fun initRvStaredSurah() {
        staredSurahAdapter = StaredSurahAdapter(requireContext())

        rv_stared_ayah.apply {
            adapter = staredSurahAdapter
            layoutManager = LinearLayoutManager(this@QuranFragment.context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
    }

    private fun fetchAllSurah(){
        fragmentQuranFragmentViewModel.fetchAllSurah()
    }

    private fun juzzSurahFilter(juzz: String){

        var datas = emptyList<Data>()

        if(juzz == "All Juzz")
            datas = allSurahDatas ?: emptyList()
        else{

            if(allSurahDatas == null)
                return

            when(juzz.toInt()){
                1 -> datas = allSurahDatas!!.filter { x -> x.number in 1..2 }
                2 -> datas = allSurahDatas!!.filter { x -> x.number == 2 }
                3 -> datas = allSurahDatas!!.filter { x -> x.number in 2..3 }
                4 -> datas = allSurahDatas!!.filter { x -> x.number in 3..4 }
                5 -> datas = allSurahDatas!!.filter { x -> x.number == 4 }
                6 -> datas = allSurahDatas!!.filter { x -> x.number in 4..5 }
                7 -> datas = allSurahDatas!!.filter { x -> x.number in 5..6 }
                8 -> datas = allSurahDatas!!.filter { x -> x.number in 6..7 }
                9 -> datas = allSurahDatas!!.filter { x -> x.number in 7..8 }
                10 -> datas = allSurahDatas!!.filter { x -> x.number in 8..9 }
                11 -> datas = allSurahDatas!!.filter { x -> x.number in 9..11 }
                12 -> datas = allSurahDatas!!.filter { x -> x.number in 11..12 }
                13 -> datas = allSurahDatas!!.filter { x -> x.number in 12..14 }
                14 -> datas = allSurahDatas!!.filter { x -> x.number in 15..16 }
                15 -> datas = allSurahDatas!!.filter { x -> x.number in 17..18 }
                16 -> datas = allSurahDatas!!.filter { x -> x.number in 18..20 }
                17 -> datas = allSurahDatas!!.filter { x -> x.number in 21..22 }
                18 -> datas = allSurahDatas!!.filter { x -> x.number in 23..25 }
                19 -> datas = allSurahDatas!!.filter { x -> x.number in 25..27 }
                20 -> datas = allSurahDatas!!.filter { x -> x.number in 27..29 }
                21 -> datas = allSurahDatas!!.filter { x -> x.number in 29..33 }
                22 -> datas = allSurahDatas!!.filter { x -> x.number in 33..36 }
                23 -> datas = allSurahDatas!!.filter { x -> x.number in 36..38 }
                24 -> datas = allSurahDatas!!.filter { x -> x.number in 39..41 }
                25 -> datas = allSurahDatas!!.filter { x -> x.number in 41..45 }
                26 -> datas = allSurahDatas!!.filter { x -> x.number in 46..51 }
                27 -> datas = allSurahDatas!!.filter { x -> x.number in 51..57 }
                28 -> datas = allSurahDatas!!.filter { x -> x.number in 58..66 }
                29 -> datas = allSurahDatas!!.filter { x -> x.number in 67..77 }
                30 -> datas = allSurahDatas!!.filter { x -> x.number in 78..144 }
            }
        }

        allSurahAdapter.setData(datas)
        allSurahAdapter.notifyDataSetChanged()

    }

    /* Refresher */
    private fun refreshLayout() {
        sl_quran.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        fetchAllSurah()
        sl_quran.isRefreshing = false
        s_juzz.setSelection(0)
    }

}