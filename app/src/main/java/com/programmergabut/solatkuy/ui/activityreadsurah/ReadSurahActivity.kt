package com.programmergabut.solatkuy.ui.activityreadsurah

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
import com.programmergabut.solatkuy.viewmodel.ViewModelFactory
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_read_surah.*

class ReadSurahActivity : AppCompatActivity() {

    companion object{
        const val surahID = "surahID"
    }

    private lateinit var readSurahViewModel: ReadSurahViewModel
    private lateinit var readSurahAdapter: ReadSurahAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_surah)

        val selSurahId = intent.getStringExtra(surahID) ?: throw Exception(Thread.currentThread().stackTrace[1].methodName)

        readSurahViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(
            this.application))[ReadSurahViewModel::class.java]

        initRVReadSurah()
        observeApi(selSurahId)

    }

    private fun observeApi(selSurahId: String){

        readSurahViewModel.selectedSurahAr.observe(this, Observer {

            when(it.Status){
                EnumStatus.SUCCESS -> {

                    if(it.data == null)
                        throw Exception(Thread.currentThread().stackTrace[1].methodName)

                    val data = it.data.data

                    readSurahAdapter.setAyah(data.ayahs)
                    readSurahAdapter.notifyDataSetChanged()

                    ab_readQuran.visibility = View.VISIBLE
                    tb_readQuran_title.title = data.englishName
                    tb_readQuran_title.subtitle = data.revelationType + " - " + data.numberOfAyahs + " Ayahs"

                    cc_readQuran_loading.animate().alpha(0.5f)
                    cc_readQuran_loading.visibility = View.GONE

                    Toasty.info(this, "Press twice on an ayah to save your last read", Toast.LENGTH_LONG).show()
                }
                EnumStatus.LOADING -> {
                    ab_readQuran.visibility = View.INVISIBLE
                    cc_readQuran_loading.visibility = View.VISIBLE
                    tb_readQuran_title.title = ""
                }
                EnumStatus.ERROR -> {
                    ab_readQuran.visibility = View.INVISIBLE
                    lottieAnimationView.cancelAnimation()
                    tv_readQuran_loading.text = getString(R.string.fetch_failed)
                }
            }
        })

        readSurahViewModel.fetchQuranSurah(selSurahId)
    }

    private fun initRVReadSurah() {
        readSurahAdapter = ReadSurahAdapter()

        rv_read_surah.apply {
            adapter = readSurahAdapter
            layoutManager = LinearLayoutManager(this@ReadSurahActivity)
            setHasFixedSize(true)
        }
    }

}