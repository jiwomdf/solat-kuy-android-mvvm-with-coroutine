package com.programmergabut.solatkuy.ui.fastrata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseActivity
import com.programmergabut.solatkuy.databinding.ActivityFastRataBinding
import com.programmergabut.solatkuy.ui.boarding.BoardingViewModel
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.Status
import kotlinx.android.synthetic.main.layout_info.rvDuaCollection

class FastRataActivity : BaseActivity<ActivityFastRataBinding, FastRataViewModel>(
    R.layout.activity_fast_rata,
    FastRataViewModel::class.java
) {
    override fun getViewBinding(): ActivityFastRataBinding =
        ActivityFastRataBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupListener()
    }

    private fun setupListener() {
        with(binding) {
            viewModel.queries.observe(this@FastRataActivity) {
                when(it.status) {
                    Status.Loading -> {

                    }
                    Status.Success -> {
                        val strBuilder = StringBuilder()
                        it.data?.forEach {
                            strBuilder.append("$it \n")
                        }
                    }
                    Status.Error -> {

                    }
                }
            }
        }
    }

    private fun setupView() {
        with(binding) {
            btnGo.setOnClickListener {
                val query = etInput.text.toString().trim()
                viewModel.getData(query)
            }
        }
    }
}