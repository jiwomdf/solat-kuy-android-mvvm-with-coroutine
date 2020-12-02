package com.programmergabut.solatkuy.ui.activityfavayah

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseActivity
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_fav_ayah.*

@AndroidEntryPoint
class FavAyahActivity : BaseActivity(R.layout.activity_fav_ayah) {

    private lateinit var favAyahAdapter: FavAyahAdapter
    private val favAyahViewModel: FavAyahViewModel by viewModels()

    override fun setFirstView() {
        tb_favAyah.title = "Ayahs you've been liked"
        initRVFavAyah()
    }

    override fun setObserver() {
        favAyahViewModel.favAyah.observe(this, {
            when(it.status){
                EnumStatus.SUCCESS -> {
                    if(it.data == null)
                        showBottomSheet(isCancelable = false, isFinish = true)

                    if(it.data?.isEmpty()!!)
                        tv_fav_ayah_empty.visibility = View.VISIBLE
                    else{
                        favAyahAdapter.listAyah = it.data
                        favAyahAdapter.notifyDataSetChanged()
                        tv_fav_ayah_empty.visibility = View.GONE
                    }
                }
                EnumStatus.LOADING -> {}
                EnumStatus.ERROR -> {
                    showBottomSheet(isCancelable = false, isFinish = true)
                }
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