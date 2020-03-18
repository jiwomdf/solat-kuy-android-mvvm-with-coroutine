package com.programmergabut.solatkuy.ui.main.view

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.api.ApiHelper
import com.programmergabut.solatkuy.data.api.ApiServiceImpl
import com.programmergabut.solatkuy.ui.base.ViewModelFactory
import com.programmergabut.solatkuy.ui.main.viewmodel.MainActivityViewModel
import com.programmergabut.solatkuy.util.EnumStatus
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_prayer_time.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var menuList: Menu? = null
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewModel()
        getTodayPrayerTimeAPI()
    }

    private fun setupViewModel() {
        mainActivityViewModel = ViewModelProviders.of(this, ViewModelFactory(ApiHelper(ApiServiceImpl())))
            .get(MainActivityViewModel::class.java)
    }

    private fun getTodayPrayerTimeAPI() {
        mainActivityViewModel.getPrayer().observe(this, Observer { it ->
            when(it.Status){

                EnumStatus.SUCCESS -> {
                    progressBar.show()
                    it.data.let {
                        val sdf = SimpleDateFormat("dd")
                        val currentDate = sdf.format(Date())
                        val timings = it?.data?.find { obj -> obj.date.gregorian.day == currentDate.toString() }?.timings

                        tv_fajr_time.text = timings?.fajr
                        tv_dhuhr_time.text = timings?.dhuhr
                        tv_asr_time.text = timings?.asr
                        tv_maghrib_time.text = timings?.maghrib
                        tv_isha_time.text = timings?.isha
                    }
                }
                EnumStatus.LOADING -> {
                    progressBar.hide()
                }
                EnumStatus.ERROR -> {
                    progressBar.hide()
                    Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
                }

            }
        })

        mainActivityViewModel.fetchPrayer()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        this.menuList = menu

        menuInflater.inflate(R.menu.bottom_navigation_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
