package com.programmergabut.solatkuy.ui.activityfavayah

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
import com.programmergabut.solatkuy.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_fav_ayah.*
import java.lang.Exception

class FavAyahActivity : AppCompatActivity() {

    private lateinit var favAyahViewModel: FavAyahViewModel
    private lateinit var favAyahAdapter: FavAyahAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav_ayah)

        favAyahViewModel = ViewModelProvider(this,
            ViewModelFactory.getInstance(this.application))[FavAyahViewModel::class.java]

        initAppBar()
        initRVFavAyah()
        observeDB()
    }

    private fun initAppBar() {
        tb_readQuran.title = "Ayahs you've been liked"
    }

    private fun observeDB() {
        favAyahViewModel.favAyah.observe(this, Observer {

            when(it.status){
                EnumStatus.SUCCESS -> {
                    if(it.data == null)
                        throw Exception("favAyahViewModel.favAyah")

                    favAyahAdapter.setAyah(it.data)
                    favAyahAdapter.notifyDataSetChanged()
                }
                EnumStatus.LOADING -> print("loading")
                EnumStatus.ERROR -> print("error")
            }

        })
    }

    private fun initRVFavAyah() {
        favAyahAdapter = FavAyahAdapter(this, favAyahViewModel)

        rv_fav_ayah.apply {
            adapter = favAyahAdapter
            layoutManager = LinearLayoutManager(this@FavAyahActivity)
            setHasFixedSize(true)
        }
    }
}