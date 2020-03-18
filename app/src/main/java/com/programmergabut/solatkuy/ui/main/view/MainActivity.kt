package com.programmergabut.solatkuy.ui.main.view

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.api.ApiHelper
import com.programmergabut.solatkuy.data.api.ApiServiceImpl
import com.programmergabut.solatkuy.data.model.prayerApi.Timings
import com.programmergabut.solatkuy.ui.base.ViewModelFactory
import com.programmergabut.solatkuy.ui.main.viewmodel.MainActivityViewModel
import com.programmergabut.solatkuy.util.EnumStatus
import kotlinx.android.synthetic.main.layout_prayer_time.*
import kotlinx.android.synthetic.main.layout_widget.*
import org.joda.time.DateTime
import org.joda.time.Period
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private var menuList: Menu? = null
    private var prayerTimings: Timings? = null
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
                    it.data.let {
                        val sdf = SimpleDateFormat("dd", Locale.US)
                        val currentDate = sdf.format(Date())
                        val timings = it?.data?.find { obj -> obj.date.gregorian.day == currentDate.toString() }?.timings

                        setPrayerText(timings)
                        setPrayerIcon()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            setupWidget(timings)

                    }
                }
                EnumStatus.LOADING -> {
                    setPrayerText(null)
                }
                EnumStatus.ERROR -> {
                    Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        })

        mainActivityViewModel.fetchPrayer()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupWidget(timings: Timings?) {

        if(timings == null)
            return

        val selPrayer = selectPrayer(timings)
        selectWidgetTitle(selPrayer)
        selectWidgetPic(selPrayer)
        selectNextPrayerTime(selPrayer, timings)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun selectNextPrayerTime(selPrayer: Int, timings: Timings) {

        val sdfPrayer = SimpleDateFormat("HH:mm", Locale.US)
        val nowTime = DateTime(sdfPrayer.parse(java.time.LocalTime.now().toString()))
        var period: Period? = null

        when(selPrayer){
            1 -> period = Period(nowTime, DateTime(sdfPrayer.parse(timings.fajr.split(" ")[0].trim())))
            2 -> period = Period(nowTime, DateTime(sdfPrayer.parse(timings.dhuhr.split(" ")[0].trim())))
            3 -> period = Period(nowTime, DateTime(sdfPrayer.parse(timings.asr.split(" ")[0].trim())))
            4 -> period = Period(nowTime, DateTime(sdfPrayer.parse(timings.maghrib.split(" ")[0].trim())))
            5 -> period = Period(nowTime, DateTime(sdfPrayer.parse(timings.isha.split(" ")[0].trim())))
        }

        if(period != null)
            tv_widget_prayer_countdown.text = "${abs(period.hours)} Hour ${abs(period.minutes)} Minute left"
    }

    private fun selectWidgetTitle(selPrayer: Int) {

        when(selPrayer){
            1 -> tv_widget_prayer_name.text = getString(R.string.fajr)
            2 -> tv_widget_prayer_name.text = getString(R.string.dhuhr)
            3 -> tv_widget_prayer_name.text = getString(R.string.asr)
            4 -> tv_widget_prayer_name.text = getString(R.string.maghrib)
            5 -> tv_widget_prayer_name.text = getString(R.string.isha)
        }
    }

    private fun selectWidgetPic(selPrayer: Int) {
        var widgetDrawable: Drawable? = null

        when(selPrayer){
            1 -> widgetDrawable = getDrawable(R.drawable.fajr)
            2 -> widgetDrawable = getDrawable(R.drawable.dhuhr)
            3 -> widgetDrawable = getDrawable(R.drawable.asr)
            4 -> widgetDrawable = getDrawable(R.drawable.mahgrib)
            5 -> widgetDrawable = getDrawable(R.drawable.isha)
        }

        if(widgetDrawable != null)
            Glide.with(this).load(widgetDrawable).centerCrop().into(iv_prayer_widget)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun selectPrayer(timings: Timings): Int {
        var prayer: Int = -1

        val sdfPrayer = SimpleDateFormat("HH:mm", Locale.US)
        val fajrTime = DateTime(sdfPrayer.parse(timings.fajr.split(" ")[0].trim()))
        val dhuhrTime =  DateTime(sdfPrayer.parse(timings.dhuhr.split(" ")[0].trim()))
        val asrTime =  DateTime(sdfPrayer.parse(timings.asr.split(" ")[0].trim()))
        val maghribTime =  DateTime(sdfPrayer.parse(timings.maghrib.split(" ")[0].trim()))
        val ishaTime =  DateTime(sdfPrayer.parse(timings.isha.split(" ")[0].trim()))

        val sunriseTime =  DateTime(sdfPrayer.parse(timings.sunrise.split(" ")[0].trim()))
        val nextfajrTime = DateTime(sdfPrayer.parse(timings.fajr.split(" ")[0].trim())).plusDays(1)

        val nowTime = DateTime(sdfPrayer.parse(java.time.LocalTime.now().toString()))

        if(fajrTime.isBefore(nowTime) && nowTime.isBefore(sunriseTime))
            prayer = 1
        else if(dhuhrTime.isBefore(nowTime) && nowTime.isBefore(asrTime))
            prayer = 2
        else if(asrTime.isBefore(nowTime) && nowTime.isBefore(maghribTime))
            prayer = 3
        else if(maghribTime.isBefore(nowTime) && nowTime.isBefore(ishaTime))
            prayer = 4
        else if(ishaTime.isBefore(nowTime) && nowTime.isBefore(nextfajrTime))
            prayer = 5

        return prayer
    }


    private fun setPrayerText(timings: Timings?) {

        if(timings == null){
            tv_fajr_time.text = getString(R.string.loading)
            tv_dhuhr_time.text = getString(R.string.loading)
            tv_asr_time.text = getString(R.string.loading)
            tv_maghrib_time.text = getString(R.string.loading)
            tv_isha_time.text = getString(R.string.loading)
        }
        else{
            tv_fajr_time.text = timings.fajr
            tv_dhuhr_time.text = timings.dhuhr
            tv_asr_time.text = timings.asr
            tv_maghrib_time.text = timings.maghrib
            tv_isha_time.text = timings.isha
        }
    }

    private fun setPrayerIcon() {
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menuList = menu

        menuInflater.inflate(R.menu.bottom_navigation_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
