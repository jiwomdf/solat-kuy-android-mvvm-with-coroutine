package com.programmergabut.solatkuy.ui.fragmentmain

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseFragment
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsFavAyah
import com.programmergabut.solatkuy.data.local.localentity.MsTimings
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.util.EnumStatus
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.EnumConfig
import com.programmergabut.solatkuy.util.helper.LocationHelper
import com.programmergabut.solatkuy.util.helper.PushNotificationHelper
import com.programmergabut.solatkuy.util.helper.SelectPrayerHelper
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.layout_popup_choose_quote_setting.view.*
import kotlinx.android.synthetic.main.layout_prayer_time.*
import kotlinx.android.synthetic.main.layout_quran_quote.*
import kotlinx.coroutines.*
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.Period
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */

@AndroidEntryPoint
class FragmentMain : BaseFragment<FragmentMainViewModel>(R.layout.fragment_main, FragmentMainViewModel::class.java){

    private var isTimerHasBinded = false
    private var coroutineTimerJob: Job? = null
    private var dialogView: View? = null
    private var tempMsApi1: MsApi1? = null
    private var mCityName: String? = null
    
    override fun onPause() {
        super.onPause()

        isTimerHasBinded = false
        coroutineTimerJob?.cancel()
        Log.d("CoroutineTimer", "Canceled.. $coroutineTimerJob ${Thread.currentThread().id}")
    }

    override fun onStart() {
        super.onStart()

        tv_widget_prayer_countdown?.text = getString(R.string.loading)
        tv_quran_ayah_quote?.visibility = View.GONE
        tv_quran_ayah_quote_click?.visibility = View.VISIBLE
    }

    override fun setFirstView() {
        openPopupQuote()
    }
    override fun setObserver() {
        subscribeObserversDB()
        subscribeObserversAPI()
    }
    override fun setListener() {
        tvQuranQuoteClick()
        cbClickListener()
        iv_refresh.setOnClickListener {
            viewModel.getMsSetting(0)
        }
    }

    private fun subscribeObserversAPI() {
        viewModel.notifiedPrayer.observe(viewLifecycleOwner, { retVal ->
            when(retVal.status){
                EnumStatus.SUCCESS -> {

                    if(retVal.data == null)
                        showBottomSheet(isCancelable = false, isFinish = true)

                    bindCheckBox(retVal.data!!)
                    updateAlarmManager(retVal.data)

                    val data = createWidgetData(retVal.data)
                    bindWidget(data)
                }
                EnumStatus.LOADING -> {
                    Toast.makeText(requireContext(), "Syncing data..", Toast.LENGTH_SHORT).show()
                    bindPrayerText(null)
                }
                EnumStatus.ERROR ->{
                    showBottomSheet(isCancelable = false, isFinish = true)
                }
                else -> {/*NO-OP*/}
            }
        })

        viewModel.getMsSetting(0)
    }

    private fun subscribeObserversDB() {

        viewModel.msApi1.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it.status){
                EnumStatus.SUCCESS -> {
                    if(it.data == null )
                        return@Observer

                    /* save temp data */
                    tempMsApi1 = it.data
                    bindWidgetLocation(it.data)
                    updateMonthAndYearMsApi1(it.data)
                    fetchPrayerApi(it.data)
                }
                EnumStatus.ERROR -> {
                    showBottomSheet(isCancelable = false, isFinish = true)
                }
                else -> {}
            }
        })

        viewModel.msSetting.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when(it.status){
                EnumStatus.SUCCESS -> {

                    if(it.data == null) return@Observer

                    if(!it.data.isUsingDBQuotes)
                        subscribeReadSurahEn()
                    else
                        subscribeFavAyah()
                }
                EnumStatus.ERROR -> {
                    showBottomSheet(isCancelable = false, isFinish = true)
                }
                else -> {}
            }
        })

    }

    private fun updateMonthAndYearMsApi1(data: MsApi1) {
        val arrDate = LocalDate.now().toString("dd/M/yyyy").split("/")
        val year = arrDate[2]
        val month = arrDate[1]

        val dbYear = data.year.toInt()
        val dbMoth = data.month.toInt()

        if(year.toInt() > dbYear && month.toInt() > dbMoth){
            viewModel.updateMsApi1MonthAndYear(1, arrDate[1], arrDate[2])
        }
    }

    private fun subscribeReadSurahEn(){
        viewModel.readSurahEn.observe(viewLifecycleOwner, { apiQuotes ->
            when(apiQuotes.status){
                EnumStatus.SUCCESS -> {
                    bindQuranQuoteApiOnline(apiQuotes)
                    viewModel.favAyah.removeObservers(this)
                }
                EnumStatus.LOADING -> {
                    tv_quran_ayah_quote_click.text = getString(R.string.loading)
                    tv_quran_ayah_quote.text = getString(R.string.loading)
                }
                EnumStatus.ERROR -> {
                    tv_quran_ayah_quote.text = getString(R.string.fetch_failed)
                    tv_quran_ayah_quote_click.text = getString(R.string.fetch_failed)
                }
            }
        })

        fetchQuranSurah()
    }

    private fun subscribeFavAyah(){
        viewModel.favAyah.observe(viewLifecycleOwner, { localQuotes ->
            when(localQuotes.status){
                EnumStatus.SUCCESS -> {
                    if(localQuotes.data == null)
                        throw Exception("favAyahViewModel.favAyah")

                    bindQuranSurahDB(localQuotes.data)
                    viewModel.readSurahEn.removeObservers(this)
                }
                EnumStatus.LOADING -> tv_quran_ayah_quote_click.text = getString(R.string.loading)
                EnumStatus.ERROR -> tv_quran_ayah_quote_click.text = getString(R.string.fetch_failed)
            }
        })

    }

    private fun createWidgetData(prayer: List<NotifiedPrayer>): MsTimings {

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
        viewModel.syncNotifiedPrayer(msApi1)
    }

    private fun fetchQuranSurah(){

        val randSurah = (1..114).random()

        viewModel.fetchReadSurahEn(randSurah)
    }

    /* Database Transaction */
    private fun updatePrayerIsNotified(prayer:String, isNotified:Boolean){

        if(isNotified)
            Toasty.success(requireContext(), "$prayer will be notified every day", Toast.LENGTH_SHORT).show()
        else
            Toasty.warning(requireContext(), "$prayer will not be notified anymore", Toast.LENGTH_SHORT).show()

        viewModel.updatePrayerIsNotified(prayer, isNotified)
    }

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
            }
        }
    }


    /* Widget */
    private fun bindWidget(data: MsTimings?) {

        if(data == null) return

        val selPrayer = SelectPrayerHelper.selectNextPrayerToInt(data)

        bindPrayerText(data)
        selectWidgetTitle(selPrayer)
        selectWidgetPic(selPrayer)
        selectNextPrayerTime(selPrayer, data)
    }

    private fun bindWidgetLocation(it: MsApi1) {
        mCityName = LocationHelper.getCity(requireContext(), it.latitude.toDouble(), it.longitude.toDouble())

        tv_view_latitude.text = it.latitude + " °N"
        tv_view_longitude.text = it.longitude + " °W"
        tv_view_city.text = mCityName ?: EnumConfig.CITY_NOT_FOUND_STR
    }

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
            -1 -> widgetDrawable = getDrawable(context?.applicationContext!!,R.drawable.img_sunrise)
            1 -> widgetDrawable = getDrawable(context?.applicationContext!!,R.drawable.img_fajr)
            2 -> widgetDrawable = getDrawable(context?.applicationContext!!,R.drawable.img_dhuhr)
            3 -> widgetDrawable = getDrawable(context?.applicationContext!!,R.drawable.img_asr)
            4 -> widgetDrawable = getDrawable(context?.applicationContext!!,R.drawable.img_maghrib)
            5 -> widgetDrawable = getDrawable(context?.applicationContext!!,R.drawable.img_isha)
            6 -> widgetDrawable = getDrawable(context?.applicationContext!!,R.drawable.img_isha)
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
                if(tv_widget_prayer_countdown != null){
                    val strHour = if(tempHour <= 9) "0$tempHour" else tempHour
                    val strMinute = if(tempMinute <= 9) "0$tempMinute" else tempMinute
                    val strSecond = if(tempSecond <= 9) "0$tempSecond" else tempSecond
                    tv_widget_prayer_countdown.text = "$strHour : $strMinute : $strSecond remaining"
                }
                else
                    return@withContext
            }

        }
    }

    private fun updateAlarmManager(listNotifiedPrayer: List<NotifiedPrayer>){
        if(mCityName == null)
            mCityName = "-"

        PushNotificationHelper(
            requireContext(),
            listNotifiedPrayer as MutableList<NotifiedPrayer>, mCityName!!
        )
    }

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

    private fun openPopupQuote(){
        iv_quote_setting.setOnClickListener {

            val dialog = Dialog(requireContext())
            dialogView = layoutInflater.inflate(R.layout.layout_popup_choose_quote_setting,null)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.setContentView(dialogView!!)
            dialog.show()

            viewModel.msSetting.observe(viewLifecycleOwner, {
                when(it.status){
                    EnumStatus.SUCCESS -> {
                        if(it.data != null){
                            if(it.data.isUsingDBQuotes)
                                dialogView?.rbg_quotesDataSource?.check(R.id.rb_fromFavQuote)
                            else
                                dialogView?.rbg_quotesDataSource?.check(R.id.rb_fromApi)
                        }
                    }
                    else -> {}
                }
            })

            dialogView?.rb_fromApi?.setOnClickListener {
                viewModel.updateIsUsingDBQuotes(false)
                dismissDialog(dialog)
            }

            dialogView?.rb_fromFavQuote?.setOnClickListener {
                viewModel.updateIsUsingDBQuotes(true)
                dismissDialog(dialog)
            }

        }
    }

    private fun dismissDialog(dialog: Dialog){
        dialog.dismiss()
        Toasty.success(requireContext(), "Success change the quotes source", Toast.LENGTH_SHORT).show()
    }

}
