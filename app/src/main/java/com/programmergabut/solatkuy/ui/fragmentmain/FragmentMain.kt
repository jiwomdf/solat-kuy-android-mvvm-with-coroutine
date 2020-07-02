package com.programmergabut.solatkuy.ui.fragmentmain

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsTimings
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
import com.programmergabut.solatkuy.util.helper.LocationHelper
import com.programmergabut.solatkuy.util.helper.PushNotificationHelper
import com.programmergabut.solatkuy.util.helper.SelectPrayerHelper
import dagger.hilt.android.AndroidEntryPoint
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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

@AndroidEntryPoint
class FragmentMain(private var isTimerHasBinded: Boolean) : Fragment(R.layout.fragment_main), SwipeRefreshLayout.OnRefreshListener  {

    private val fragmentMainViewModel: FragmentMainViewModel by viewModels()

    @Inject lateinit var db: SolatKuyRoom

    private var coroutineTimerJob: Job? = null

    private var dialogView: View? = null
    private var tempMsApi1: MsApi1? = null
    private var mCityName: String? = null

    override fun onStop() {
        super.onStop()

        isTimerHasBinded = false
        coroutineTimerJob?.cancel()
        Log.d("CoroutineTimer", "CANCELED.. $coroutineTimerJob ${Thread.currentThread().id}")
    }

    override fun onStart() {
        super.onStart()

        if(tv_widget_prayer_countdown != null)
            tv_widget_prayer_countdown.text = getString(R.string.loading)

        tv_quran_ayah_quote.visibility = View.GONE
        tv_quran_ayah_quote_click.visibility = View.VISIBLE

        subscribeObserversDB()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObserversAPI()

        cbClickListener()
        refreshLayout()
        tvQuranQuoteClick()
        openPopupQuote()

        updateMonthAndYearMsApi1()
    }


    /* Subscribe live data */
    private fun subscribeObserversAPI() {

        fragmentMainViewModel.notifiedPrayer.observe(viewLifecycleOwner, androidx.lifecycle.Observer { retVal ->
            when(retVal.status){
                EnumStatus.SUCCESS -> {

                    if(retVal.data == null)
                        throw Exception("notifiedPrayer return null")

                    /* Bind Checkbox*/
                    bindCheckBox(retVal.data)

                    /* Update Alarm Manager*/
                    updateAlarmManager(retVal.data)

                    /* Bind Widget*/
                    val data = createWidgetData(retVal.data)
                    bindWidget(data)
                }
                EnumStatus.LOADING -> {
                    Toasty.info(requireContext(), "syncing data..", Toast.LENGTH_SHORT).show()
                    bindPrayerText(null)
                }
                EnumStatus.ERROR -> {
                    if(retVal.data == null)
                        throw Exception("notifiedPrayer return null")

                    Toasty.warning(requireContext(), "using offline data", Toast.LENGTH_SHORT).show()

                    /* Bind Checkbox*/
                    bindCheckBox(retVal.data)

                    /* Update Alarm Manager*/
                    updateAlarmManager(retVal.data)

                    /* Bind Widget*/
                    val data = createWidgetData(retVal.data)
                    bindWidget(data)

                }
            }
        })

        fragmentMainViewModel.fetchMsSetting(0)
    }

    private fun subscribeObserversDB() {

        /* fragmentMainViewModel.listNotifiedPrayer.observe(this, androidx.lifecycle.Observer {

            /* save temp data */
            tempListPL = it

            bindCheckBox(it)
            createModelPrayer(it)?.let { data -> updateAlarmManager(data) }
        }) */

        fragmentMainViewModel.msApi1.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it.status){
                EnumStatus.SUCCESS -> {
                    if(it.data == null )
                        return@Observer

                    /* save temp data */
                    tempMsApi1 = it.data

                    bindWidgetLocation(tempMsApi1!!)

                    /* fetching Prayer API */
                    fetchPrayerApi(tempMsApi1!!)
                }
                EnumStatus.LOADING -> {}
                EnumStatus.ERROR -> {}
            }
        })

        fragmentMainViewModel.msSetting.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it.status){
                EnumStatus.SUCCESS -> {

                    if(it.data == null)
                        return@Observer

                    if(!it.data.isUsingDBQuotes)
                        subscribeReadSurahEn()
                    else
                        subscribeFavAyah()
                }
                EnumStatus.LOADING -> {}
                EnumStatus.ERROR -> {}
            }
        })

    }

    private fun updateMonthAndYearMsApi1() {
        lifecycleScope.launch {
            val arrDate = LocalDate.now().toString("dd/M/yyyy").split("/")
            db.msApi1Dao().updateMsApi1MonthAndYear(1, arrDate[1], arrDate[2])
        }
    }

    private fun subscribeReadSurahEn(){
        fragmentMainViewModel.readSurahEn.observe(viewLifecycleOwner, androidx.lifecycle.Observer { apiQuotes ->
            when(apiQuotes.status){
                EnumStatus.SUCCESS -> {

                    bindQuranQuoteApiOnline(apiQuotes)

                    fragmentMainViewModel.favAyah.removeObservers(this)
                }
                EnumStatus.LOADING -> tv_quran_ayah_quote_click.text = getString(R.string.loading)
                EnumStatus.ERROR -> tv_quran_ayah_quote_click.text = getString(R.string.fetch_failed)
            }
        })

        fetchQuranSurah()
    }

    private fun subscribeFavAyah(){
        fragmentMainViewModel.favAyah.observe(viewLifecycleOwner, androidx.lifecycle.Observer { localQuotes ->

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

    private fun createWidgetData(prayer: List<NotifiedPrayer>): MsTimings {
        /* Dagger Injection */
        /* based from sync API data */

        val arrDate = LocalDate.now().toString("dd/MMM/yyyy").split("/")

        return MsTimings(
            prayer[0].prayerTime,
            prayer[1].prayerTime,
            prayer[2].prayerTime,
            prayer[3].prayerTime,
            prayer[4].prayerTime,
            prayer[5].prayerTime,
        "",
        "",
        "",
            arrDate[0],
            arrDate[1]
        )
    }


    /* fetch API data */
    private fun fetchPrayerApi(msApi1: MsApi1) {
        fragmentMainViewModel.syncNotifiedPrayer(msApi1)
    }

    /* private fun getMsSetting(){
        fragmentMainViewModel.getMsSetting()
    } */

    private fun fetchQuranSurah(){

        val randSurah = (1..114).random()

        fragmentMainViewModel.fetchQuranSurah(randSurah)
    }

    /* Database Transaction */
    private fun updatePrayerIsNotified(prayer:String, isNotified:Boolean){

        if(isNotified)
            Toasty.success(requireContext(), "$prayer will be notified every day", Toast.LENGTH_SHORT).show()
        else
            Toasty.warning(requireContext(), "$prayer will not be notified anymore", Toast.LENGTH_SHORT).show()

        fragmentMainViewModel.updatePrayerIsNotified(prayer, isNotified)
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
    private fun bindWidget(data: MsTimings?) {

        if(data == null)
            return

        val selPrayer = SelectPrayerHelper.selectNextPrayerToInt(data)

        bindPrayerText(data)
        selectWidgetTitle(selPrayer)
        selectWidgetPic(selPrayer)
        selectNextPrayerTime(selPrayer, data)
    }

    @SuppressLint("SetTextI18n")
    private fun bindWidgetLocation(it: MsApi1) {
        mCityName = LocationHelper.getCity(requireContext(), it.latitude.toDouble(), it.longitude.toDouble())

        tv_view_latitude.text = it.latitude + " °N"
        tv_view_longitude.text = it.longitude + " °W"
        tv_view_city.text = mCityName ?: EnumConfig.lCity
    }

    @SuppressLint("SetTextI18n")
    private fun bindPrayerText(apiData: MsTimings?) {

        if(apiData == null){
            tv_fajr_time.text = getString(R.string.loading)
            tv_dhuhr_time.text = getString(R.string.loading)
            tv_asr_time.text = getString(R.string.loading)
            tv_maghrib_time.text = getString(R.string.loading)
            tv_isha_time.text = getString(R.string.loading)
            tv_year_change.text = getString(R.string.loading)
        }
        else{
            tv_fajr_time.text = apiData.fajr
            tv_dhuhr_time.text = apiData.dhuhr
            tv_asr_time.text = apiData.asr
            tv_maghrib_time.text = apiData.maghrib
            tv_isha_time.text = apiData.isha
            tv_year_change.text = "${apiData.month} ${apiData.day} "
        }
    }

    private fun selectNextPrayerTime(selPrayer: Int, timings: MsTimings) {

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

        if(!isTimerHasBinded) {
            coroutineTimerJob = lifecycleScope.launch(Dispatchers.IO) {
                coroutineTimer(this, period.hours, period.minutes, 60 - nowTime.secondOfMinute)
            }
            isTimerHasBinded = true
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

    private fun bindQuranQuoteApiOnline(retVal: Resource<ReadSurahEnResponse>) {
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
    private suspend fun coroutineTimer(
        scope: CoroutineScope,
        hour: Int,
        minute: Int,
        second: Int
    ){
        
        var tempHour = abs(hour)
        var tempMinute = abs(minute)
        var tempSecond = abs(second)

        var isMinuteZero = false

        while(true){

            if(scope.isActive){
                Log.d("CoroutineTimer", "Running.. $scope $tempSecond ${Thread.currentThread().id}")
            }
            else{
                Log.d("CoroutineTimer", "Stopping.. $scope $tempSecond ${Thread.currentThread().id}")
                coroutineTimerJob?.cancel()
                break
            }

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

                withContext(Dispatchers.Main){
                    tempMsApi1?.let { fetchPrayerApi(it) }
                }
            }

            withContext(Dispatchers.Main){
                if(tv_widget_prayer_countdown != null)
                    tv_widget_prayer_countdown.text = "$tempHour : $tempMinute : $tempSecond remaining"
                else
                    return@withContext
            }

        }
    }

    /* Alarm Manager & Notification */
    private fun updateAlarmManager(listNotifiedPrayer: List<NotifiedPrayer>){

        if(mCityName == null)
            mCityName = "-"

        PushNotificationHelper(
            requireContext(),
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
        fragmentMainViewModel.fetchMsSetting(0)
        sl_main.isRefreshing = false
    }

    /* Popup quotes setting */
    private fun openPopupQuote(){
        iv_quote_setting.setOnClickListener {

            val dialog = Dialog(requireContext())
            dialogView = layoutInflater.inflate(R.layout.layout_popup_choose_quote_setting,null)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.setContentView(dialogView!!)
            dialog.show()

            fragmentMainViewModel.msSetting.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                when(it.status){
                    EnumStatus.SUCCESS -> {
                        if(it.data != null){
                            if(it.data.isUsingDBQuotes)
                                dialogView?.rbg_quotesDataSource?.check(R.id.rb_fromFavQuote)
                            else
                                dialogView?.rbg_quotesDataSource?.check(R.id.rb_fromApi)
                        }
                    }
                    EnumStatus.LOADING -> {}
                    EnumStatus.ERROR -> {}
                }
            })

            dialogView?.rb_fromApi?.setOnClickListener {
                fragmentMainViewModel.updateIsUsingDBQuotes(false)
                dismissDialog(dialog)
            }

            dialogView?.rb_fromFavQuote?.setOnClickListener {
                fragmentMainViewModel.updateIsUsingDBQuotes(true)
                dismissDialog(dialog)
            }

        }
    }

    private fun dismissDialog(dialog: Dialog){
        dialog.dismiss()
        //getMsSetting()
        Toasty.success(requireContext(), "Success change the quotes source", Toast.LENGTH_SHORT).show()
    }

}
