package com.programmergabut.solatkuy.ui.fragmentmain.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Data
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Timings
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnApi
import com.programmergabut.solatkuy.di.component.DaggerIDataComponent
import com.programmergabut.solatkuy.di.component.DaggerITimingsComponent
import com.programmergabut.solatkuy.di.module.DataModule
import com.programmergabut.solatkuy.ui.fragmentmain.CoroutineTimerProvider
import com.programmergabut.solatkuy.ui.fragmentmain.viewmodel.FragmentMainViewModel
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
import com.programmergabut.solatkuy.util.generator.SurahQuoteGenerator
import com.programmergabut.solatkuy.util.helper.LocationHelper
import com.programmergabut.solatkuy.util.helper.PushNotificationHelper
import com.programmergabut.solatkuy.util.helper.SelectPrayerHelper
import com.programmergabut.solatkuy.viewmodel.ViewModelFactory
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.layout_popup_choose_quote_setting.view.*
import kotlinx.android.synthetic.main.layout_prayer_time.*
import kotlinx.android.synthetic.main.layout_quran_quote.*
import kotlinx.android.synthetic.main.layout_widget.*
import kotlinx.coroutines.*
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.Period
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

class FragmentMain : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var fragmentMainViewModel: FragmentMainViewModel
    private lateinit var coroutineTimerProvider: CoroutineTimerProvider
    private var dialogView: View? = null
    private var tempMsApi1: MsApi1? = null
    private var mCityName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentMainViewModel = ViewModelProvider(this, ViewModelFactory
            .getInstance(activity?.application!!))[FragmentMainViewModel::class.java]

        subscribeObserversAPI()
        subscribeObserversDB()
    }

    override fun onPause() {
        super.onPause()

        coroutineTimerProvider.Main.cancel()
        coroutineTimerProvider.IO.cancel()
    }


    override fun onStart() {
        super.onStart()

        coroutineTimerProvider = CoroutineTimerProvider.getInstance()

        if(tv_widget_prayer_countdown != null)
            tv_widget_prayer_countdown.text = getString(R.string.loading)

        tv_quran_ayah_quote.visibility = View.GONE
        tv_quran_ayah_quote_click.visibility = View.VISIBLE

        loadTempData()
        subscribeObserversDB()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cbClickListener()
        refreshLayout()
        tvQuranQuoteClick()
        openPopupQuote()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_main, container, false)


    /* Subscribe live data */
    private fun subscribeObserversAPI() {

        fragmentMainViewModel.notifiedPrayer.observe(this, androidx.lifecycle.Observer { retVal ->
            when(retVal.status){
                EnumStatus.SUCCESS -> {

                    if(retVal.data == null)
                        throw Exception("notifiedPrayer return null")

                    /* Bind Checkbox*/
                    bindCheckBox(retVal.data)

                    /* Update Alarm Manager*/
                    updateAlarmManager(retVal.data)

                    /* Bind Widget*/
                    val data = createData(retVal.data)
                    bindWidget(data)

                }
                EnumStatus.LOADING -> {
                    Toasty.info(context!!, "syncing data..", Toast.LENGTH_SHORT).show()
                    bindPrayerText(null)
                }
                EnumStatus.ERROR -> {
                    if(retVal.data == null)
                        throw Exception("notifiedPrayer return null")

                    Toasty.warning(context!!, "using offline data", Toast.LENGTH_SHORT).show()

                    /* Bind Checkbox*/
                    bindCheckBox(retVal.data)

                    /* Update Alarm Manager*/
                    updateAlarmManager(retVal.data)

                    /* Bind Widget*/
                    val data = createData(retVal.data)
                    bindWidget(data)

                }
            }
        })

    }

    private fun subscribeObserversDB() {

        /* fragmentMainViewModel.listNotifiedPrayer.observe(this, androidx.lifecycle.Observer {

            /* save temp data */
            tempListPL = it

            bindCheckBox(it)
            createModelPrayer(it)?.let { data -> updateAlarmManager(data) }
        }) */

        fragmentMainViewModel.msApi1Local.observe(this, androidx.lifecycle.Observer {

            if(it == null )
                return@Observer

            /* save temp data */
            tempMsApi1 = it

            bindWidgetLocation(it)

            /* fetching Prayer API */
            fetchPrayerApi(it)
            fetchQuranSurah()
        })

        fragmentMainViewModel.msSetting.observe(this, androidx.lifecycle.Observer {

            if(!it.isUsingDBQuotes){
                fragmentMainViewModel.readSurahEn.observe(this, androidx.lifecycle.Observer { apiQuotes ->
                    when(apiQuotes.status){
                        EnumStatus.SUCCESS -> {

                            if(apiQuotes.data == null)
                                bindQuranQuoteApiOffline()
                            else
                                bindQuranQuoteApiOnline(apiQuotes)

                            fragmentMainViewModel.favAyah.removeObservers(this)
                        }
                        EnumStatus.LOADING -> tv_quran_ayah_quote_click.text = getString(R.string.loading)
                        EnumStatus.ERROR -> bindQuranQuoteApiOffline()
                    }
                })
            }
            else{
                fragmentMainViewModel.favAyah.observe(this, androidx.lifecycle.Observer { localQuotes ->

                    when(localQuotes.status){
                        EnumStatus.SUCCESS -> {
                            if(localQuotes.data == null)
                                throw Exception("favAyahViewModel.favAyah")

                            bindQuranSurahDB(localQuotes.data)
                            fragmentMainViewModel.readSurahEn.removeObservers(this)
                        }
                        EnumStatus.LOADING -> tv_quran_ayah_quote_click.text = getString(R.string.loading)
                        EnumStatus.ERROR -> tv_quran_ayah_quote_click.text = getString(R.string.fetch_failed)
                    }

                })
            }

        })
    }

    private fun createData(prayer: List<NotifiedPrayer>): Data {
        /* Dagger Injection */
        /* based from sync API data */
        val localTimings = DaggerITimingsComponent.builder()
            .fajr(prayer[0].prayerTime /* fajr */)
            .dhuhr(prayer[1].prayerTime /* dhuhr */)
            .asr(prayer[2].prayerTime /* asr*/)
            .maghrib(prayer[3].prayerTime /* mahgrib */)
            .isha(prayer[4].prayerTime /* isha */)
            .sunrise(prayer[5].prayerTime /* isha */)
            .imsak("")
            .midnight("")
            .sunset("")
            .build()
            .getTimings()

        val arrDate = LocalDate.now().toString("dd/MMM/yyyy").split("/")

        return DaggerIDataComponent.builder()
            .dataModule(DataModule(localTimings, arrDate[0], arrDate[1],0))
            .build()
            .getData()
    }


    /* fetch API data */
    private fun fetchPrayerApi(msApi1: MsApi1) {
        fragmentMainViewModel.notifiedPrayer.postValue(Resource.loading(null))
        fragmentMainViewModel.fetchPrayerApi(msApi1)
    }

    private fun fetchQuranSurah(){
        fragmentMainViewModel.readSurahEn.postValue(Resource.loading(null))

        val randSurah = (1..114).random()

        fragmentMainViewModel.fetchQuranSurah(randSurah)
    }

    /* Database Transaction */
    private fun updatePrayerIsNotified(prayer:String, isNotified:Boolean){

        if(isNotified)
            Toasty.success(context!!, "$prayer will be notified every day", Toast.LENGTH_SHORT).show()
        else
            Toasty.warning(context!!, "$prayer will not be notified anymore", Toast.LENGTH_SHORT).show()

        fragmentMainViewModel.updatePrayerIsNotified(prayer, isNotified)
    }


    /* init onStart data */
    private fun loadTempData() {
        tempMsApi1?.let {
            fetchPrayerApi(it)
            fetchQuranSurah()
            bindWidgetLocation(it)
        }
    }

    /* private fun createModelPrayer(it: List<NotifiedPrayer>): MutableList<NotifiedPrayer>? {

        val list = mutableListOf<NotifiedPrayer>()
        var modelNotifiedPrayer: NotifiedPrayer? = null

        if(tempApiData == null || tempApiData?.timings == null)
            return null

        it.forEach con@{ p ->
            when (p.prayerName) {
                getString(R.string.fajr) -> modelNotifiedPrayer =
                    NotifiedPrayer(
                        1,
                        p.prayerName,
                        p.isNotified,
                        tempApiData?.timings?.fajr!!
                    )
                getString(R.string.dhuhr) -> modelNotifiedPrayer =
                    NotifiedPrayer(
                        2,
                        p.prayerName,
                        p.isNotified,
                        tempApiData?.timings?.dhuhr!!
                    )
                getString(R.string.asr) -> modelNotifiedPrayer =
                    NotifiedPrayer(
                        3,
                        p.prayerName,
                        p.isNotified,
                        tempApiData?.timings?.asr!!
                    )
                getString(R.string.maghrib) -> modelNotifiedPrayer =
                    NotifiedPrayer(
                        4,
                        p.prayerName,
                        p.isNotified,
                        tempApiData?.timings?.maghrib!!
                    )
                getString(R.string.isha) -> modelNotifiedPrayer =
                    NotifiedPrayer(
                        5,
                        p.prayerName,
                        p.isNotified,
                        tempApiData?.timings?.isha!!
                    )
                getString(R.string.sunrise) -> modelNotifiedPrayer =
                    NotifiedPrayer(
                        6,
                        p.prayerName,
                        p.isNotified,
                        tempApiData?.timings?.sunrise!!
                    )
            }

            list.add(modelNotifiedPrayer!!)
        }

        return list
    } */

    private fun cbClickListener() {

        cb_fajr.setOnClickListener {
            if(cb_fajr.isChecked)
                updatePrayerIsNotified(getString(R.string.fajr), true)
            else
                updatePrayerIsNotified(getString(R.string.fajr), false)
        }

        cb_dhuhr.setOnClickListener {
            if(cb_dhuhr.isChecked)
                updatePrayerIsNotified(getString(R.string.dhuhr), true)
            else
                updatePrayerIsNotified(getString(R.string.dhuhr), false)
        }

        cb_asr.setOnClickListener {
            if(cb_asr.isChecked)
                updatePrayerIsNotified(getString(R.string.asr), true)
            else
                updatePrayerIsNotified(getString(R.string.asr), false)
        }

        cb_maghrib.setOnClickListener {
            if(cb_maghrib.isChecked)
                updatePrayerIsNotified(getString(R.string.maghrib), true)
            else
                updatePrayerIsNotified(getString(R.string.maghrib), false)
        }

        cb_isha.setOnClickListener {
            if(cb_isha.isChecked)
                updatePrayerIsNotified(getString(R.string.isha), true)
            else
                updatePrayerIsNotified(getString(R.string.isha), false)
        }

    }

    private fun bindCheckBox(list: List<NotifiedPrayer>) {
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
    private fun bindWidget(data: Data?) {

        if(data == null)
            return

        val selPrayer = SelectPrayerHelper.selectNextPrayerToInt(data.timings)

        bindPrayerText(data)
        selectWidgetTitle(selPrayer)
        selectWidgetPic(selPrayer)
        selectNextPrayerTime(selPrayer, data.timings)
    }

    @SuppressLint("SetTextI18n")
    private fun bindWidgetLocation(it: MsApi1) {
        mCityName = LocationHelper.getCity(context!!, it.latitude.toDouble(), it.longitude.toDouble())

        tv_view_latitude.text = it.latitude + " °N"
        tv_view_longitude.text = it.longitude + " °W"
        tv_view_city.text = mCityName ?: EnumConfig.lCity
    }

    @SuppressLint("SetTextI18n")
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
            tv_year_change.text = "${apiData.date.gregorian?.month?.en} ${apiData.date.gregorian?.day} "
        }
    }

    private fun selectNextPrayerTime(selPrayer: Int, timings: Timings) {

        val sdfPrayer = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        val nowTime = DateTime(sdfPrayer.parse(org.joda.time.LocalTime.now().toString()))
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

        GlobalScope.launch(coroutineTimerProvider.IO){
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

    private fun bindQuranQuoteApiOffline(){
        val ayah = SurahQuoteGenerator.retAyah()
        tv_quran_ayah_quote_click.text = if(ayah.length > 100) ayah.substring(0, 100) + "..." else ayah
        tv_quran_ayah_quote.text = ayah
    }

    private fun bindQuranQuoteApiOnline(retVal: Resource<ReadSurahEnApi>) {
        val returnValue = retVal.data?.data!!
        val randAyah = (returnValue.ayahs.indices).random()
        val ayah = returnValue.ayahs[randAyah].text + " - QS " + returnValue.englishName + " Ayah " + returnValue.numberOfAyahs

        tv_quran_ayah_quote_click.text = if(ayah.length > 100) ayah.substring(0, 100) + "..." else ayah
        tv_quran_ayah_quote.text = ayah
    }

    private fun bindQuranSurahDB(listAyah: List<MsFavAyah>) {

        if(listAyah.isNotEmpty()){
            val randAyah = (listAyah.indices).random()
            val data = listAyah[randAyah]
            val ayah = data.ayahEn + " - QS " + data.surahName + " Ayah " + data.ayahID

            tv_quran_ayah_quote_click.text = if(ayah.length > 100) ayah.substring(0, 100) + "..." else ayah
            tv_quran_ayah_quote.text = ayah
        }
        else{
            tv_quran_ayah_quote_click.text = getString(R.string.youHaventFavAnyAyah)
            tv_quran_ayah_quote.text = getString(R.string.youHaventFavAnyAyah)
        }

    }

    /* Coroutine Timer */
    @SuppressLint("SetTextI18n")
    private suspend fun coroutineTimer(hour: Int, minute: Int, second: Int){
        
        var tempHour = abs(hour)
        var tempMinute = abs(minute)
        var tempSecond = abs(second)

        var isCountDownOn = true
        var isMinuteZero = false

        while (isCountDownOn){
            delay(1000)
            tempSecond--

            if(tempSecond == 0){
                tempSecond = 60

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

                GlobalScope.launch(coroutineTimerProvider.Main){
                    tempMsApi1?.let { fetchPrayerApi(it) }
                }
            }

            GlobalScope.launch(coroutineTimerProvider.Main){
                if(tv_widget_prayer_countdown != null)
                    tv_widget_prayer_countdown.text = "$tempHour : $tempMinute : $tempSecond remaining"
                else
                    isCountDownOn = false
            }

        }
    }

    /* Alarm Manager & Notification */
    private fun updateAlarmManager(listNotifiedPrayer: List<NotifiedPrayer>){

        if(mCityName == null)
            mCityName = "-"

        PushNotificationHelper(
            context!!,
            listNotifiedPrayer as MutableList<NotifiedPrayer>, mCityName!!
        )
        //Toasty.info(context!!, "alarm manager updated", Toast.LENGTH_SHORT).show()
    }

    /* tv Quran Quote Click */
    private fun tvQuranQuoteClick() {
        tv_quran_ayah_quote_click.setOnClickListener {
            it.visibility = View.GONE
            tv_quran_ayah_quote.visibility = View.VISIBLE
        }
        tv_quran_ayah_quote.setOnClickListener {
            tv_quran_ayah_quote_click.visibility = View.VISIBLE
            it.visibility = View.GONE
        }
    }

    /* Refresher */
    private fun refreshLayout() {
        sl_main.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        loadTempData()
        sl_main.isRefreshing = false
    }

    /* Popup quotes setting */
    private fun openPopupQuote(){
        iv_quote_setting.setOnClickListener {

            val dialog = Dialog(context!!)
            dialogView = layoutInflater.inflate(R.layout.layout_popup_choose_quote_setting,null)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.setContentView(dialogView!!)
            dialog.show()

            fragmentMainViewModel.msSetting.observe(this, androidx.lifecycle.Observer {

                if(it.isUsingDBQuotes)
                    dialogView?.rbg_quotesDataSource?.check(R.id.rb_fromFavQuote)
                else
                    dialogView?.rbg_quotesDataSource?.check(R.id.rb_fromApi)

            })

            dialogView?.rb_fromApi?.setOnClickListener {
                fragmentMainViewModel.updateIsUsingDBQuotes(false)
            }

            dialogView?.rb_fromFavQuote?.setOnClickListener {
                fragmentMainViewModel.updateIsUsingDBQuotes(true)
            }

        }
    }

}
