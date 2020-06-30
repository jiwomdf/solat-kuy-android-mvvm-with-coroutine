package com.programmergabut.solatkuy.ui.activityfavayah

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.viewModels
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_fav_ayah.*
import java.lang.Exception

@AndroidEntryPoint
class FavAyahActivity : AppCompatActivity() {

    private lateinit var favAyahAdapter: FavAyahAdapter
    private val favAyahViewModel: FavAyahViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav_ayah)

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

                    if(it.data.isEmpty())
                        tv_fav_ayah_empty.visibility = View.VISIBLE
                    else{
                        favAyahAdapter.setAyah(it.data)
                        favAyahAdapter.notifyDataSetChanged()
                        tv_fav_ayah_empty.visibility = View.GONE
                    }

                }
                EnumStatus.LOADING -> {}
                EnumStatus.ERROR -> {}
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