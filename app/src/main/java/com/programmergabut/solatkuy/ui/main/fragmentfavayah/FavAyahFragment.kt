package com.programmergabut.solatkuy.ui.main.fragmentfavayah

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseFragment
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.databinding.FragmentFavAyahBinding
import com.programmergabut.solatkuy.databinding.LayoutDeleteBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavAyahFragment constructor(
    viewModelTest: FavAyahViewModel? = null
): BaseFragment<FragmentFavAyahBinding, FavAyahViewModel>(
    R.layout.fragment_fav_ayah,
    FavAyahViewModel::class.java, viewModelTest
) {
    private lateinit var favAyahAdapter: FavAyahAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tbFavAyah.title = getString(R.string.ayahs_you_have_been_like)
        initRVFavAyah()
        setListener()
    }

    override fun setListener() {
        viewModel.favAyah.observe(viewLifecycleOwner, {
            if (it == null){
                showBottomSheet(isCancelable = false, isFinish = true)
                return@observe
            }
            if (it.isEmpty()) {
                binding.tvFavAyahEmpty.visibility = View.VISIBLE
            } else {
                favAyahAdapter.listAyah = it
                favAyahAdapter.notifyDataSetChanged()
                binding.tvFavAyahEmpty.visibility = View.GONE
            }
        })
    }

    private fun initRVFavAyah() {
        favAyahAdapter = FavAyahAdapter(onClickFavAyah)
        binding.rvFavAyah.apply {
            adapter = favAyahAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private val onClickFavAyah = fun(data: MsFavAyah) {
        val dialog = Dialog(requireContext())
        val dialogView = DataBindingUtil.inflate<LayoutDeleteBinding>(layoutInflater, R.layout.layout_delete, null, true)
        dialog.apply {
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setContentView(dialogView.root)
            show()
        }
        dialogView.btnUnfavorite.setOnClickListener {
            viewModel.deleteFavAyah(data)
            dialog.hide()
        }
        dialogView.btnCancel.setOnClickListener {
            dialog.hide()
        }
    }

}