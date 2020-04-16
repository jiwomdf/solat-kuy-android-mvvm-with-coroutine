package com.programmergabut.solatkuy.ui.fragmentmain.view

import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.data.model.entity.PrayerLocal
import com.programmergabut.solatkuy.data.model.prayerJson.*
import com.programmergabut.solatkuy.di.component.DaggerIDataComponent
import com.programmergabut.solatkuy.di.component.DaggerITimingsComponent
import com.programmergabut.solatkuy.di.module.DataModule
import com.programmergabut.solatkuy.ui.fragmentmain.viewmodel.FragmentMainViewModel
import com.programmergabut.solatkuy.util.EnumStatus
import com.programmergabut.solatkuy.util.PushNotificationHelper
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.SelectPrayerHelper
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.layout_prayer_time.*
import kotlinx.android.synthetic.main.layout_widget.*
import kotlinx.coroutines.*
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.Period
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Date
import kotlin.math.abs

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentMain : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var fragmentMainViewModel: FragmentMainViewModel
    private var tempApiData: Data? = null
    private var tempMsApi1: MsApi1? = null
    private var tempListPL: List<PrayerLocal>? = null
    private var mCityName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentMainViewModel = ViewModelProviders.of(this).get(FragmentMainViewModel::class.java)

        subscribeObserversAPI()
        subscribeObserversDB()
    }

    override fun onStart() {
        super.onStart()

        loadTempData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cbClickListener()
        refreshLayout()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_main, container, false)


    /* Subscribe live data */
    private fun subscribeObserversDB() {

        val currDate = LocalDate()

        fragmentMainViewModel.listPrayerLocal.observe(this, androidx.lifecycle.Observer {

            /* save temp data */
            tempListPL = it

            bindCheckBox(it)
            createModelPrayer(it)?.let { data -> updateAlarmManager(data) }
        })

        fragmentMainViewModel.msApi1Local.observe(this, androidx.lifecycle.Observer {

            if(it == null )
                return@Observer

            /* save temp data */
            tempMsApi1 = it

            bindWidgetLocation(it)

            /* fetching Prayer API */
            fetchPrayerApi(it.latitude,it.longitude,"8", currDate.monthOfYear.toString(),currDate.year.toString())
        })
    }

    private fun subscribeObserversAPI() {

        val sdf = SimpleDateFormat("dd", Locale.getDefault())
        val currentDate = sdf.format(Date())

        fragmentMainViewModel.prayerApi.observe(this, androidx.lifecycle.Observer { it ->
            when(it.Status){
                EnumStatus.SUCCESS -> {
                    it.data.let {

                        /* save temp data */
                        tempApiData = createOnlineData(it, currentDate)
                        bindWidget(tempApiData)
                    }}
                EnumStatus.LOADING -> Toasty.info(context!!, "fetching data..", Toast.LENGTH_SHORT).show()
                EnumStatus.ERROR -> { Toasty.info(context!!,"offline mode", Toast.LENGTH_SHORT).show()

                    /* save temp data */
                    tempApiData = createOfflineData()
                    bindWidget(tempApiData)
                }
            }
        })

    }

    /* Create data from API */
    private fun createOnlineData(it: PrayerApi?, currentDate: String): Data? {
        return it?.data?.find { obj -> obj.date.gregorian.day == currentDate }
    }

    private fun createOfflineData(): Data {
        /* Dagger Injection */
        /* based from saved API data */
        val localTimings = DaggerITimingsComponent.builder()
            .fajr(tempListPL!![0].prayerTime /* fajr */)
            .dhuhr(tempListPL!![1].prayerTime /* dhuhr */)
            .asr(tempListPL!![2].prayerTime /* asr*/)
            .maghrib(tempListPL!![3].prayerTime /* mahgrib */)
            .isha(tempListPL!![4].prayerTime /* isha */)
            .sunrise(tempListPL!![5].prayerTime /* isha */)
            .imsak("")
            .midnight("")
            .sunset("")
            .build()
            .getTimings()

        val arrDate = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy")).split("/")

        return DaggerIDataComponent.builder()
            .dataModule(DataModule(localTimings, arrDate[1], arrDate[0],0))
            .build()
            .getData()
    }


    /* fetch API data */
    private fun fetchPrayerApi(latitude: String, longitude: String, method: String, month: String, year: String) {
        fragmentMainViewModel.fetchPrayerApi(latitude,longitude,method, month,year)
    }


    /* Database Transaction */
    private fun updateNotifiedPrayerWithoutTime(prayer:String, isNotified:Boolean){

        if(isNotified)
            Toasty.success(context!!, "$prayer will be notified every day", Toast.LENGTH_SHORT).show()
        else
            Toasty.warning(context!!, "$prayer will not be notified anymore", Toast.LENGTH_SHORT).show()

        fragmentMainViewModel.updatePrayerIsNotified(prayer, isNotified)
    }


    /* init first load data */
    private fun createModelPrayer(it: List<PrayerLocal>): MutableList<PrayerLocal>? {

        val list = mutableListOf<PrayerLocal>()
        var modelPrayer: PrayerLocal? = null

        if(tempApiData == null || tempApiData?.timings == null)
            return null

        it.forEach con@{ p ->
            when (p.prayerName) {
                getString(R.string.fajr) -> modelPrayer = PrayerLocal(1 ,p.prayerName, p.isNotified, tempApiData?.timings?.fajr!!)
                getString(R.string.dhuhr) -> modelPrayer = PrayerLocal(2 ,p.prayerName, p.isNotified, tempApiData?.timings?.dhuhr!!)
                getString(R.string.asr) -> modelPrayer = PrayerLocal(3 ,p.prayerName, p.isNotified, tempApiData?.timings?.asr!!)
                getString(R.string.maghrib) -> modelPrayer = PrayerLocal(4 ,p.prayerName, p.isNotified, tempApiData?.timings?.maghrib!!)
                getString(R.string.isha) -> modelPrayer = PrayerLocal(5, p.prayerName, p.isNotified, tempApiData?.timings?.isha!!)
                getString(R.string.sunrise) -> modelPrayer = PrayerLocal(6, p.prayerName, p.isNotified, tempApiData?.timings?.sunrise!!)
            }

            list.add(modelPrayer!!)
        }

        return list
    }

    private fun loadTempData() {
        if(tempApiData != null)
            bindWidget(tempApiData)
        if(tempMsApi1 != null)
            bindWidgetLocation(tempMsApi1!!)
    }

    private fun refreshLayout() {
        sl_main.setOnRefreshListener(this)
    }

    private fun cbClickListener() {

        cb_fajr.setOnClickListener {
            if(cb_fajr.isChecked)
                updateNotifiedPrayerWithoutTime(getString(R.string.fajr), true)
            else
                updateNotifiedPrayerWithoutTime(getString(R.string.fajr), false)
        }

        cb_dhuhr.setOnClickListener {
            if(cb_dhuhr.isChecked)
                updateNotifiedPrayerWithoutTime(getString(R.string.dhuhr), true)
            else
                updateNotifiedPrayerWithoutTime(getString(R.string.dhuhr), false)
        }

        cb_asr.setOnClickListener {
            if(cb_asr.isChecked)
                updateNotifiedPrayerWithoutTime(getString(R.string.asr), true)
            else
                updateNotifiedPrayerWithoutTime(getString(R.string.asr), false)
        }

        cb_maghrib.setOnClickListener {
            if(cb_maghrib.isChecked)
                updateNotifiedPrayerWithoutTime(getString(R.string.maghrib), true)
            else
                updateNotifiedPrayerWithoutTime(getString(R.string.maghrib), false)
        }

        cb_isha.setOnClickListener {
            if(cb_isha.isChecked)
                updateNotifiedPrayerWithoutTime(getString(R.string.isha), true)
            else
                updateNotifiedPrayerWithoutTime(getString(R.string.isha), false)
        }

    }

    private fun bindCheckBox(list: List<PrayerLocal>) {
        list.forEach {
            when {
                it.prayerName.trim() == getString(R.string.fajr) && it.isNotified -> cb_fajr.isChecked = true
                it.prayerName.trim() == getString(R.string.dhuhr) && it.isNotified -> cb_dhuhr.isChecked = true
                it.prayerName.trim() == getString(R.string.asr) && it.isNotified -> cb_asr.isChecked = true
                it.prayerName.trim() == getString(R.string.maghrib) && it.isNotified -> cb_maghrib.isChecked = true
                it.prayerName.trim() == getString(R.string.isha) && it.isNotified -> cb_isha.isChecked = true
            }}
    }


    /* Widget */
    private fun bindWidgetLocation(it: MsApi1) {
        val geoCoder = Geocoder(context!!, Locale.getDefault())
        try {
            val addresses: List<Address> = geoCoder.getFromLocation(it.latitude.toDouble(),it.longitude.toDouble(), 1)
            mCityName = addresses[0].subAdminArea
        }
        catch (ex: Exception){
            mCityName = "-"
        }

        tv_view_latitude.text = it.latitude + " °N"
        tv_view_longitude.text = it.longitude + " °W"
        tv_view_city.text = mCityName
    }

    private fun bindPrayerText(apiData: Data?) {

        if(apiData == null){
            tv_fajr_time.text = getString(R.string.loading)
            tv_dhuhr_time.text = getString(R.string.loading)
            tv_asr_time.text = getString(R.string.loading)
            tv_maghrib_time.text = getString(R.string.loading)
            tv_isha_time.text = getString(R.string.loading)
            tv_year_change.text = getString(R.string.loading)
        }
        else{
            tv_fajr_time.text = apiData.timings.fajr
            tv_dhuhr_time.text = apiData.timings.dhuhr
            tv_asr_time.text = apiData.timings.asr
            tv_maghrib_time.text = apiData.timings.maghrib
            tv_isha_time.text = apiData.timings.isha
            tv_year_change.text = "${apiData.date.gregorian.month?.en} ${apiData.date.gregorian.day} "
        }
    }

    private fun bindWidget(apiData: Data?) {

        if(apiData == null)
            return

        val selPrayer = SelectPrayerHelper.selectNextPrayerToInt(apiData.timings)

        bindPrayerText(apiData)
        selectWidgetTitle(selPrayer)
        selectWidgetPic(selPrayer)
        selectNextPrayerTime(selPrayer, apiData.timings)
    }

    private fun selectNextPrayerTime(selPrayer: Int, timings: Timings) {

        val sdfPrayer = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val nowTime = DateTime(sdfPrayer.parse(LocalTime.now().toString()))
        var period: Period? = null

        when(selPrayer){
            -1 -> period = Period(nowTime, DateTime(sdfPrayer.parse(timings.dhuhr.split(" ")[0].trim() + ":00")))
            1 -> period = Period(nowTime, DateTime(sdfPrayer.parse(timings.sunrise.split(" ")[0].trim()+ ":00")))
            2 -> period = Period(nowTime, DateTime(sdfPrayer.parse(timings.asr.split(" ")[0].trim()+ ":00")))
            3 -> period = Period(nowTime, DateTime(sdfPrayer.parse(timings.maghrib.split(" ")[0].trim()+ ":00")))
            4 -> period = Period(nowTime, DateTime(sdfPrayer.parse(timings.isha.split(" ")[0].trim()+ ":00")))
            5 -> period = Period(nowTime, DateTime(sdfPrayer.parse(timings.fajr.split(" ")[0].trim()+ ":00")).plusDays(1))
            6 -> period = Period(nowTime, DateTime(sdfPrayer.parse(timings.fajr.split(" ")[0].trim()+ ":00")))
        }

        if(period == null)
            return

        CoroutineScope(Dispatchers.Default).launch{
            coroutineTimer(period.hours, period.minutes, 60 - nowTime.secondOfMinute)
        }

    }

    private fun selectWidgetTitle(selPrayer: Int) {

        when(selPrayer){
            -1 -> tv_widget_prayer_name.text = getString(R.string.next_prayer_is_dhuhr)
            1 -> tv_widget_prayer_name.text = getString(R.string.fajr)
            2 -> tv_widget_prayer_name.text = getString(R.string.dhuhr)
            3 -> tv_widget_prayer_name.text = getString(R.string.asr)
            4 -> tv_widget_prayer_name.text = getString(R.string.maghrib)
            5 -> tv_widget_prayer_name.text = getString(R.string.isha)
            6 -> tv_widget_prayer_name.text = getString(R.string.isha)
        }
    }

    private fun selectWidgetPic(selPrayer: Int) {
        var widgetDrawable: Drawable? = null

        when(selPrayer){
            -1 -> widgetDrawable = getDrawable(context?.applicationContext!!,R.drawable.sunrise)
            1 -> widgetDrawable = getDrawable(context?.applicationContext!!,R.drawable.fajr)
            2 -> widgetDrawable = getDrawable(context?.applicationContext!!,R.drawable.dhuhr)
            3 -> widgetDrawable = getDrawable(context?.applicationContext!!,R.drawable.asr)
            4 -> widgetDrawable = getDrawable(context?.applicationContext!!,R.drawable.maghrib)
            5 -> widgetDrawable = getDrawable(context?.applicationContext!!,R.drawable.isha)
            6 -> widgetDrawable = getDrawable(context?.applicationContext!!,R.drawable.isha)
        }

        if(widgetDrawable != null){
            val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

            Glide.with(this)
                .load(widgetDrawable)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .transition(DrawableTransitionOptions.withCrossFade(factory))
                .into(iv_prayer_widget)
        }
    }

    /* Coroutine Timer */
    private suspend fun coroutineTimer(hour: Int, minute: Int, second: Int){
        
        var tempHour = abs(hour)
        var tempMinute = abs(minute)
        var tempSecond = abs(second)

        var onOffCountDown = true
        var isMinuteZero = false

        while (onOffCountDown){
            delay(1000)
            tempSecond--

            if(tempSecond == 0){
                tempSecond = 59

                if(tempMinute != 0)
                    tempMinute -= 1

                if(tempMinute == 0)
                    isMinuteZero = true
            }

            if(tempMinute == 0){

                if(!isMinuteZero)
                    tempMinute = 59

                if(tempHour != 0)
                    tempHour -= 1
            }

            /* fetching Prayer API */
            if(tempHour == 0 && tempMinute == 0 && tempSecond == 1){

                withContext(Dispatchers.Main){
                    tv_widget_prayer_countdown.text = ""
                    delay(1000)

                    tempMsApi1?.let {
                        fetchPrayerApi(it.latitude, it.longitude, "8", it.month, it.year)
                    }
                }
            }

            withContext(Dispatchers.Main){
                if(tv_widget_prayer_countdown != null)
                    tv_widget_prayer_countdown.text = "$tempHour : $tempMinute : $tempSecond remaining"
                else
                    onOffCountDown = false
            }

        }
    }

    /* Alarm Manager & Notification */
    private fun updateAlarmManager(listPrayer: MutableList<PrayerLocal>){

        if(mCityName == null)
            mCityName = "-"

        PushNotificationHelper(context!!, listPrayer, mCityName!!)
        //Toasty.info(context!!, "alarm manager updated", Toast.LENGTH_SHORT).show()
    }

    /* Refresher */
    override fun onRefresh() {
        val currDate = LocalDate()

        tempMsApi1?.let {
            fragmentMainViewModel.prayerApi.postValue(Resource.loading(null))
            fetchPrayerApi(it.latitude, it.longitude, "8", currDate.monthOfYear.toString(),currDate.year.toString())
        }

        sl_main.isRefreshing = false
    }


}
