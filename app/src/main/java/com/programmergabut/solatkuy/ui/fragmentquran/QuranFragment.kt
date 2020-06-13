package com.programmergabut.solatkuy.ui.fragmentquran

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
import com.programmergabut.solatkuy.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_quran.*
import java.util.*

class QuranFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var fragmentQuranFragmentViewModel: QuranFragmentViewModel
    private lateinit var allSurahAdapter: AllSurahAdapter
    private var allSurahDatas: MutableList<Data>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentQuranFragmentViewModel = ViewModelProvider(this, ViewModelFactory
            .getInstance(activity?.application!!))[QuranFragmentViewModel::class.java]

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_quran, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRvAllSurah()
        observeApi()
        refreshLayout()
        searchSurah()
        createJuzzSpinner()
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
        fragmentQuranFragmentViewModel.allSurah.observe(this, Observer {
            when(it.Status){
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

        fetchfetchAllSurah()
    }

    private fun createJuzzSpinner(){
        val arrJuzz = mutableListOf<String>()
        arrJuzz.add("All Juzz")
        for (i in 1..30){ arrJuzz.add(i.toString()) }

        s_juzz.adapter = ArrayAdapter(context!!, R.layout.spinner_item, arrJuzz)

        s_juzz.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                juzzSurahFilter(s_juzz.selectedItem.toString())
            }
        }

    }

    private fun initRvAllSurah() {
        allSurahAdapter = AllSurahAdapter(context!!)

        rv_quran_surah.apply {
            adapter = allSurahAdapter
            layoutManager = LinearLayoutManager(this@QuranFragment.context)
            setHasFixedSize(true)
        }
    }

    private fun fetchfetchAllSurah(){
        fragmentQuranFragmentViewModel.allSurah.postValue(Resource.loading(null))
        fragmentQuranFragmentViewModel.fetchAllSurah("triger")
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
        fetchfetchAllSurah()
        sl_quran.isRefreshing = false
        s_juzz.setSelection(0)
    }

}