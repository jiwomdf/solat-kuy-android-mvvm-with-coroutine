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
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.remote.remoteentity.quranallsurahJson.Data
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
import com.programmergabut.solatkuy.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_quran.*
import java.util.*

class QuranFragment : Fragment() {

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

        searchSurah()
        createJuzzSpinner()

    }

    private fun searchSurah() {
        et_search.addTextChangedListener(object: TextWatcher{

            override fun afterTextChanged(s: Editable?) {
                val newData = allSurahDatas!!.filter { x -> x.englishNameLC!!.contains(s.toString()) }

                allSurahAdapter.setData(newData)
                allSurahAdapter.notifyDataSetChanged()
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
                }
                EnumStatus.LOADING -> {
                    tv_loading_all_surah.visibility = View.VISIBLE
                    tv_loading_all_surah.text = getString(R.string.loading)
                }
                EnumStatus.ERROR -> tv_loading_all_surah.text = getString(R.string.fetch_failed)
            }
        })
    }

    private fun createJuzzSpinner(){
        val arrJuzz = mutableListOf<String>()
        arrJuzz.add("All Juzz")
        for (i in 1..30){ arrJuzz.add(i.toString()) }

        s_juzz.adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, arrJuzz)

        s_juzz.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                juzzSurahFilter(s_juzz.selectedItem.toString())
            }
        }
    }

    private fun initRvAllSurah() {
        allSurahAdapter = AllSurahAdapter()

        rv_quran_surah.apply {
            adapter = allSurahAdapter
            layoutManager = LinearLayoutManager(this@QuranFragment.context)
            setHasFixedSize(true)
        }
    }

    private fun juzzSurahFilter(juzz: String){

        var datas = emptyList<Data>()

        if(juzz == "All Juzz")
            datas = allSurahDatas ?: emptyList()
        else{

            if(allSurahDatas == null)
                return

            when(juzz.toInt()){
                2 -> datas = allSurahDatas!!.filter { x -> x.number == 2 }
                4 -> datas = allSurahDatas!!.filter { x -> x.number == 4 }
                in 1..2 -> datas = allSurahDatas!!.filter { x -> x.number in 1..2 }
                in 2..3 -> datas = allSurahDatas!!.filter { x -> x.number in 2..3 }
                in 3..4 -> datas = allSurahDatas!!.filter { x -> x.number in 3..4 }
                in 4..5 -> datas = allSurahDatas!!.filter { x -> x.number in 4..5 }
                in 5..6 -> datas = allSurahDatas!!.filter { x -> x.number in 5..6 }
                in 6..7 -> datas = allSurahDatas!!.filter { x -> x.number in 6..7 }
                in 7..8 -> datas = allSurahDatas!!.filter { x -> x.number in 7..8 }
                in 8..9 -> datas = allSurahDatas!!.filter { x -> x.number in 8..9 }
                in 9..11 -> datas = allSurahDatas!!.filter { x -> x.number in 9..11 }
                in 11..12 -> datas = allSurahDatas!!.filter { x -> x.number in 11..12 }
                in 12..14 -> datas = allSurahDatas!!.filter { x -> x.number in 12..14 }
                in 15..16 -> datas = allSurahDatas!!.filter { x -> x.number in 15..16 }
                in 17..18 -> datas = allSurahDatas!!.filter { x -> x.number in 17..18 }
                in 18..20 -> datas = allSurahDatas!!.filter { x -> x.number in 18..20 }
                in 21..22 -> datas = allSurahDatas!!.filter { x -> x.number in 21..22 }
                in 23..25 -> datas = allSurahDatas!!.filter { x -> x.number in 23..25 }
                in 25..27 -> datas = allSurahDatas!!.filter { x -> x.number in 25..27 }
                in 27..29 -> datas = allSurahDatas!!.filter { x -> x.number in 27..29 }
                in 29..33 -> datas = allSurahDatas!!.filter { x -> x.number in 29..33 }
                in 33..36 -> datas = allSurahDatas!!.filter { x -> x.number in 33..36 }
                in 36..38 -> datas = allSurahDatas!!.filter { x -> x.number in 36..38 }
                in 39..41 -> datas = allSurahDatas!!.filter { x -> x.number in 39..41 }
                in 41..45 -> datas = allSurahDatas!!.filter { x -> x.number in 41..45 }
                in 46..51 -> datas = allSurahDatas!!.filter { x -> x.number in 46..51 }
                in 51..57 -> datas = allSurahDatas!!.filter { x -> x.number in 51..57 }
                in 58..66 -> datas = allSurahDatas!!.filter { x -> x.number in 58..66 }
                in 67..77 -> datas = allSurahDatas!!.filter { x -> x.number in 67..77 }
                in 78..144 -> datas = allSurahDatas!!.filter { x -> x.number in 78..144 }
            }
        }

        allSurahAdapter.setData(datas)
        allSurahAdapter.notifyDataSetChanged()

    }

}