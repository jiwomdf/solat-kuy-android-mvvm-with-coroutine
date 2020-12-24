package com.programmergabut.solatkuy.ui.activityfavayah

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseActivity
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.databinding.ActivityFavAyahBinding
import com.programmergabut.solatkuy.databinding.LayoutDeleteBinding
import com.programmergabut.solatkuy.util.EnumStatus
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavAyahActivity : BaseActivity<ActivityFavAyahBinding, FavAyahViewModel>(
    R.layout.activity_fav_ayah, FavAyahViewModel::class.java,
) {
    private lateinit var favAyahAdapter: FavAyahAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tbFavAyah.title = "Ayahs you've been liked"
        initRVFavAyah()
        setListener()
    }

    override fun setListener() {
        viewModel.favAyah().observe(this, {
            when (it.status) {
                EnumStatus.SUCCESS -> {
                    if (it.data == null)
                        showBottomSheet(isCancelable = false, isFinish = true)

                    if (it.data?.isEmpty()!!) {
                        binding.tvFavAyahEmpty.visibility = View.VISIBLE
                    } else {
                        favAyahAdapter.listAyah = it.data
                        favAyahAdapter.notifyDataSetChanged()
                        binding.tvFavAyahEmpty.visibility = View.GONE
                    }
                }
                EnumStatus.LOADING -> { }
                EnumStatus.ERROR -> showBottomSheet(isCancelable = false, isFinish = true)
            }
        })
    }
    private fun initRVFavAyah() {
        favAyahAdapter = FavAyahAdapter(onClickFavAyah)
        binding.rvFavAyah.apply {
            adapter = favAyahAdapter
            layoutManager = LinearLayoutManager(this@FavAyahActivity)
            setHasFixedSize(true)
        }
    }

    private val onClickFavAyah = fun(data: MsFavAyah) {
        val dialog = Dialog(this@FavAyahActivity)

        val dialogView = DataBindingUtil.inflate<LayoutDeleteBinding>(
            layoutInflater, R.layout.layout_delete, null, true
        )
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setContentView(dialogView.root)
        dialog.show()

        dialogView.btnUnfavorite.setOnClickListener {
            viewModel.deleteFavAyah(data)
            dialog.hide()
        }

        dialogView.btnCancel.setOnClickListener {
            dialog.hide()
        }
    }
}