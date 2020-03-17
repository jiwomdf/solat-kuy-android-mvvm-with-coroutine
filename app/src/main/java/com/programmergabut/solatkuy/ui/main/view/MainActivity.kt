package com.programmergabut.solatkuy.ui.main.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.api.ApiHelper
import com.programmergabut.solatkuy.data.api.ApiServiceImpl
import com.programmergabut.solatkuy.data.model.prayerApi.Data
import com.programmergabut.solatkuy.data.model.prayerApi.PrayerApi
import com.programmergabut.solatkuy.ui.base.ViewModelFactory
import com.programmergabut.solatkuy.ui.main.adapter.MainAdapter
import com.programmergabut.solatkuy.ui.main.viewmodel.MainViewModel
import com.programmergabut.solatkuy.util.EnumStatus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MainAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI()
        setupViewModel()
        setupAPICall()
    }

    private fun setupUI(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter =  MainAdapter(arrayListOf())
        recyclerView.adapter = adapter
    }

    private fun setupViewModel(){
        mainViewModel = ViewModelProviders.of(this, ViewModelFactory(ApiHelper(ApiServiceImpl())))
            .get(MainViewModel::class.java)
    }

    private fun setupAPICall(){
        mainViewModel.getPrayer().observe(this, Observer {

            when(it.Status){
                EnumStatus.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    it.data?.let { prayer -> renderList(prayer)}
                    recyclerView.visibility = View.VISIBLE
                }
                EnumStatus.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                EnumStatus.ERROR -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                }
            }
        })

        mainViewModel.fetchPrayer()
    }

    private fun renderList(prayer: PrayerApi) {
        adapter.addData(prayer)
        adapter.notifyDataSetChanged()
    }

}
