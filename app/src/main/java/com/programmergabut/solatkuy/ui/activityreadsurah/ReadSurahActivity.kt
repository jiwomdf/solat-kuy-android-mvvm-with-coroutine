package com.programmergabut.solatkuy.ui.activityreadsurah

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.Ayah
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonAr.ReadSurahArApi
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
import com.programmergabut.solatkuy.viewmodel.ViewModelFactory
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

        val selSurahId = intent.getStringExtra(surahID)

        readSurahViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(
            this.application))[ReadSurahViewModel::class.java]

        initRVReadSurah()
        selSurahId?.let { observeApi(selSurahId) }

    }

    private fun observeApi(selSurahId: String){

        readSurahViewModel.selectedSurahAr.observe(this, Observer {

            when(it.Status){
                EnumStatus.SUCCESS -> {

                    if(it.data == null)
                        throw Exception("recyler view READ SURAH ACTIVITY")

                    readSurahAdapter.setAyah(it.data.data.ayahs)
                    readSurahAdapter.notifyDataSetChanged()

                    cc_readQuran_loading.animate().alpha(0.5f)
                    cc_readQuran_loading.visibility = View.GONE
                }
                EnumStatus.LOADING -> cc_readQuran_loading.visibility = View.VISIBLE
                EnumStatus.ERROR -> {
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