package com.programmergabut.solatkuy.ui.activityfavayah

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.ui.activityreadsurah.ReadSurahAdapter
import com.programmergabut.solatkuy.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_fav_ayah.*
import kotlinx.android.synthetic.main.activity_read_surah.*

class FavAyahActivity : AppCompatActivity() {

    private lateinit var favAyahViewModel: FavAyahViewModel
    private lateinit var favAyahAdapter: FavAyahAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav_ayah)

        favAyahViewModel = ViewModelProvider(this,
            ViewModelFactory.getInstance(this.application))[FavAyahViewModel::class.java]

        initRVFavAyah()
        observeDB()
    }

    private fun observeDB() {
        favAyahViewModel.favAyah.observe(this, Observer {
            favAyahAdapter.setAyah(it)
            favAyahAdapter.notifyDataSetChanged()
        })
    }

    private fun initRVFavAyah() {
        favAyahAdapter = FavAyahAdapter()

        rv_fav_ayah.apply {
            adapter = favAyahAdapter
            layoutManager = LinearLayoutManager(this@FavAyahActivity)
            setHasFixedSize(true)
        }
    }
}