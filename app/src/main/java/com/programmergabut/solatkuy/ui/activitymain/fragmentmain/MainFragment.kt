package com.programmergabut.solatkuy.ui.activitymain.fragmentmain

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Data
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.remoteentity.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.databinding.*
import com.programmergabut.solatkuy.ui.activitymain.fragmentmain.adapter.DuaCollectionAdapter
import com.programmergabut.solatkuy.util.EnumStatus
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.EnumConfig
import com.programmergabut.solatkuy.util.EnumConfig.Companion.ENDED_SURAH
import com.programmergabut.solatkuy.util.EnumConfig.Companion.STARTED_SURAH
import com.programmergabut.solatkuy.util.generator.DuaGenerator
import com.programmergabut.solatkuy.util.helper.LocationHelper
import com.programmergabut.solatkuy.util.helper.PushNotificationHelper
import com.programmergabut.solatkuy.util.helper.SelectPrayerHelper
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
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
class MainFragment(viewModelTest: FragmentMainViewModel? = null) : BaseFragment<FragmentMainBinding, FragmentMainViewModel>(
    R.layout.fragment_main, FragmentMainViewModel::class.java, viewModelTest
){

    private var isTimerHasBinded = false
    private var coroutineTimerJob: Job? = null
    private var tempMsApi1: MsApi1? = null
    private var mCityName: String? = null
    private lateinit var duaCollectionAdapter: DuaCollectionAdapter

    override fun onPause() {
        super.onPause()

        isTimerHasBinded = false
        coroutineTimerJob?.cancel()
        Log.d("CoroutineTimer", "Canceled.. $coroutineTimerJob ${Thread.currentThread().id}")
    }

    override fun onStart() {
        super.onStart()

        binding.tvWidgetPrayerCountdown.text = getString(R.string.loading)
        binding.includeQuranQuote?.tvQuranAyahQuote.visibility = View.GONE
        binding.includeQuranQuote?.tvQuranAyahQuoteClick.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openPopupQuote()
        initRvDuaCollection()
        subscribeObserversDB()
        subscribeObserversAPI()
    }

    override fun setListener() {
        super.setListener()
        tvQuranQuoteClick()
        cbClickListener()
        binding.includeQuranQuote.ivRefresh.setOnClickListener {
            viewModel.getMsSetting(0)
        }
    }

    private fun initRvDuaCollection() {
        duaCollectionAdapter = DuaCollectionAdapter(requireContext())
        duaCollectionAdapter.setData(DuaGenerator.getListDua())

        binding.includeInfo.rvDuaCollection.apply {
            adapter = duaCollectionAdapter
            layoutManager = LinearLayoutManager(this@MainFragment.context)
            setHasFixedSize(true)
        }
    }

    private fun subscribeObserversAPI() {
        viewModel.notifiedPrayer.observe(viewLifecycleOwner, { retVal ->
            when(retVal.status){
                EnumStatus.SUCCESS -> {

                    if(retVal.data == null)
                        showBottomSheet(isCancelable = false, isFinish = true)
                    if(retVal.data?.isEmpty()!!)
                        return@observe

                    bindCheckBox(retVal.data)
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
            }
        })

        viewModel.prayer.observe(viewLifecycleOwner, {

            when(it.status){
                EnumStatus.SUCCESS -> {
                    val sdf = SimpleDateFormat("dd", Locale.getDefault())
                    val currentDate = sdf.format(Date())
                    val data = createTodayData(it.data, currentDate)
                    val date = data?.date
                    val hijriDate = date?.hijri
                    val gregorianDate = date?.gregorian

                    binding.includeInfo.tvImsakDate.text = date?.readable
                    binding.includeInfo.tvImsakTime.text = data?.timings?.imsak
                    binding.includeInfo.tvGregorianDate.text = gregorianDate?.date
                    binding.includeInfo.tvHijriDate.text = hijriDate?.date
                    binding.includeInfo.tvGregorianMonth.text = gregorianDate?.month?.en
                    binding.includeInfo.tvHijriMonth.text = hijriDate?.month?.en + " / " + hijriDate?.month?.ar
                    binding.includeInfo.tvGregorianDay.text = gregorianDate?.weekday?.en
                    binding.includeInfo.tvHijriDay.text = hijriDate?.weekday?.en + " / " + hijriDate?.weekday?.ar
                }
                EnumStatus.LOADING -> {
                    setState(it.status)
                }
                EnumStatus.ERROR ->{
                    showBottomSheet(description = getString(R.string.fetch_failed), isCancelable = true, isFinish = false)
                    setState(it.status)
                }
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
                    syncNotifiedPrayer(it.data)
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
                    if(it.data == null)
                        return@Observer

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

        viewModel.msApi1.observe(viewLifecycleOwner, { retval ->
            when(retval.status){
                EnumStatus.SUCCESS -> {
                    if(retval.data == null)
                        showBottomSheet(isCancelable = false, isFinish = true)

                    val city = LocationHelper.getCity(requireContext(), retval.data!!.latitude.toDouble(), retval.data.longitude.toDouble())

                    binding.includeInfo.tvCity.text = city ?: EnumConfig.CITY_NOT_FOUND_STR
                    fetchPrayerApi(retval.data)
                }
                EnumStatus.ERROR -> showBottomSheet(isCancelable = false, isFinish = true)
                else -> {/*NO-OP*/}
            }
        })

    }

    private fun setState(status: EnumStatus){
        when(status){
            EnumStatus.SUCCESS -> { }
            EnumStatus.LOADING -> {
                binding.includeInfo.tvImsakDate.text = getString(R.string.loading)
                binding.includeInfo.tvImsakTime.text = getString(R.string.loading)
                binding.includeInfo.tvGregorianDate.text = getString(R.string.loading)
                binding.includeInfo.tvHijriDate.text = getString(R.string.loading)
                binding.includeInfo.tvGregorianMonth.text = getString(R.string.loading)
                binding.includeInfo.tvHijriMonth.text = getString(R.string.loading)
                binding.includeInfo.tvGregorianDay.text = getString(R.string.loading)
                binding.includeInfo.tvHijriDay.text = getString(R.string.loading)
            }
            EnumStatus.ERROR ->{
                binding.includeInfo.tvImsakDate.text = getString(R.string.fetch_failed)
                binding.includeInfo.tvImsakTime.text = getString(R.string.fetch_failed_sort)
                binding.includeInfo.tvGregorianDate.text = getString(R.string.fetch_failed_sort)
                binding.includeInfo.tvHijriDate.text = getString(R.string.fetch_failed_sort)
                binding.includeInfo.tvGregorianMonth.text = getString(R.string.fetch_failed_sort)
                binding.includeInfo.tvHijriMonth.text = getString(R.string.fetch_failed_sort)
                binding.includeInfo.tvGregorianDay.text = getString(R.string.fetch_failed_sort)
                binding.includeInfo.tvHijriDay.text = getString(R.string.fetch_failed_sort)
            }
            else -> {/*NO-OP*/}
        }
    }

    private fun createTodayData(it: PrayerResponse?, currentDate: String): Data? {
        return it?.data?.find { obj -> obj.date.gregorian?.day == currentDate }
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
                    binding.includeQuranQuote.tvQuranAyahQuote.text = getString(R.string.loading)
                    binding.includeQuranQuote.tvQuranAyahQuoteClick.text = getString(R.string.loading)
                }
                EnumStatus.ERROR -> {
                    binding.includeQuranQuote.tvQuranAyahQuote.text = getString(R.string.fetch_failed)
                    binding.includeQuranQuote.tvQuranAyahQuoteClick.text = getString(R.string.fetch_failed)
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
                EnumStatus.LOADING -> binding.includeQuranQuote.tvQuranAyahQuote.text = getString(R.string.loading)
                EnumStatus.ERROR -> binding.includeQuranQuote.tvQuranAyahQuoteClick.text = getString(R.string.fetch_failed)
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
    private fun syncNotifiedPrayer(msApi1: MsApi1) {
        viewModel.syncNotifiedPrayer(msApi1)
    }

    private fun fetchPrayerApi(mMsApi1: MsApi1) {
        viewModel.fetchPrayerApi(mMsApi1)
    }

    private fun fetchQuranSurah(){
        val randSurah = (STARTED_SURAH..ENDED_SURAH).random()
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

        binding.includePrayerTime.cbFajr.setOnClickListener {
            if(binding.includePrayerTime.cbFajr.isChecked)
                updatePrayerIsNotified(getString(R.string.fajr), true)
            else
                updatePrayerIsNotified(getString(R.string.fajr), false)
        }

        binding.includePrayerTime.cbDhuhr.setOnClickListener {
            if(binding.includePrayerTime.cbDhuhr.isChecked)
                updatePrayerIsNotified(getString(R.string.dhuhr), true)
            else
                updatePrayerIsNotified(getString(R.string.dhuhr), false)
        }

        binding.includePrayerTime.cbAsr.setOnClickListener {
            if(binding.includePrayerTime.cbAsr.isChecked)
                updatePrayerIsNotified(getString(R.string.asr), true)
            else
                updatePrayerIsNotified(getString(R.string.asr), false)
        }

        binding.includePrayerTime.cbMaghrib.setOnClickListener {
            if(binding.includePrayerTime.cbMaghrib.isChecked)
                updatePrayerIsNotified(getString(R.string.maghrib), true)
            else
                updatePrayerIsNotified(getString(R.string.maghrib), false)
        }

        binding.includePrayerTime.cbIsha.setOnClickListener {
            if(binding.includePrayerTime.cbIsha.isChecked)
                updatePrayerIsNotified(getString(R.string.isha), true)
            else
                updatePrayerIsNotified(getString(R.string.isha), false)
        }

    }

    private fun bindCheckBox(list: List<NotifiedPrayer>) {

        list.forEach {
            when {
                it.prayerName.trim() == getString(R.string.fajr) && it.isNotified -> binding.includePrayerTime.cbFajr.isChecked = true
                it.prayerName.trim() == getString(R.string.dhuhr) && it.isNotified -> binding.includePrayerTime.cbDhuhr.isChecked = true
                it.prayerName.trim() == getString(R.string.asr) && it.isNotified -> binding.includePrayerTime.cbAsr.isChecked = true
                it.prayerName.trim() == getString(R.string.maghrib) && it.isNotified -> binding.includePrayerTime.cbMaghrib.isChecked = true
                it.prayerName.trim() == getString(R.string.isha) && it.isNotified -> binding.includePrayerTime.cbIsha.isChecked = true
            }
        }
    }


    /* Widget */
    private fun bindWidget(data: MsTimings?) {

        if(data == null) return

        val selectedPrayer = SelectPrayerHelper.selectNextPrayerToInt(data)

        bindPrayerText(data)
        selectWidgetTitle(selectedPrayer)
        selectWidgetPic(selectedPrayer)
        selectNextPrayerTime(selectedPrayer, data)
    }

    private fun bindWidgetLocation(it: MsApi1) {
        mCityName = LocationHelper.getCity(requireContext(), it.latitude.toDouble(), it.longitude.toDouble())

        binding.tvViewLatitude.text = it.latitude + " °N"
        binding.tvViewLongitude.text = it.longitude + " °W"
        binding.tvViewCity.text = mCityName ?: EnumConfig.CITY_NOT_FOUND_STR
    }

    private fun bindPrayerText(apiData: MsTimings?) {

        if(apiData == null){
            binding.includePrayerTime.tvFajrTime.text = getString(R.string.loading)
            binding.includePrayerTime.tvDhuhrTime.text = getString(R.string.loading)
            binding.includePrayerTime.tvAsrTime.text = getString(R.string.loading)
            binding.includePrayerTime.tvMaghribTime.text = getString(R.string.loading)
            binding.includePrayerTime.tvIshaTime.text = getString(R.string.loading)
            binding.includePrayerTime.tvDateChange.text = getString(R.string.loading)
        }
        else{
            binding.includePrayerTime.tvFajrTime.text = apiData.fajr
            binding.includePrayerTime.tvDhuhrTime.text = apiData.dhuhr
            binding.includePrayerTime.tvAsrTime.text = apiData.asr
            binding.includePrayerTime.tvMaghribTime.text = apiData.maghrib
            binding.includePrayerTime.tvIshaTime.text = apiData.isha
            binding.includePrayerTime.tvDateChange.text = "${apiData.month} ${apiData.day} "
        }
    }

    private fun selectNextPrayerTime(selectedPrayer: Int, timings: MsTimings) {

        val sdfPrayer = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        val nowTime = DateTime(sdfPrayer.parse(org.joda.time.LocalTime.now().toString()))
        var period: Period? = null

        when(selectedPrayer){
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

    private fun selectWidgetTitle(selectedPrayer: Int) {

        when(selectedPrayer){
            -1 -> binding.tvWidgetPrayerName.text = getString(R.string.next_prayer_is_dhuhr)
            1 -> binding.tvWidgetPrayerName.text = getString(R.string.fajr)
            2 -> binding.tvWidgetPrayerName.text = getString(R.string.dhuhr)
            3 -> binding.tvWidgetPrayerName.text = getString(R.string.asr)
            4 -> binding.tvWidgetPrayerName.text = getString(R.string.maghrib)
            5 -> binding.tvWidgetPrayerName.text = getString(R.string.isha)
            6 -> binding.tvWidgetPrayerName.text = getString(R.string.isha)
        }
    }

    private fun selectWidgetPic(selectedPrayer: Int) {
        var widgetDrawable: Drawable? = null

        when(selectedPrayer){
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
                .into(binding.ivPrayerWidget)
        }
    }

    private fun bindQuranQuoteApiOnline(retVal: Resource<ReadSurahEnResponse>) {
        val returnValue = retVal.data?.data!!
        val randAyah = (returnValue.ayahs.indices).random()
        val ayah = returnValue.ayahs[randAyah].text + " - QS " + returnValue.englishName + " Ayah " + returnValue.numberOfAyahs

        binding.includeQuranQuote.tvQuranAyahQuote.text = if(ayah.length > 100) ayah.substring(0, 100) + "..." else ayah
        binding.includeQuranQuote.tvQuranAyahQuoteClick.text = ayah
    }

    private fun bindQuranSurahDB(listAyah: List<MsFavAyah>) {

        if(listAyah.isNotEmpty()){
            val randAyah = (listAyah.indices).random()
            val data = listAyah[randAyah]
            val ayah = data.ayahEn + " - QS " + data.surahName + " Ayah " + data.ayahID

            binding.includeQuranQuote.tvQuranAyahQuote.text = if(ayah.length > 100) ayah.substring(0, 100) + "..." else ayah
            binding.includeQuranQuote.tvQuranAyahQuoteClick.text = ayah
        }
        else{
            binding.includeQuranQuote.tvQuranAyahQuote.text = getString(R.string.youHaventFavAnyAyah)
            binding.includeQuranQuote.tvQuranAyahQuoteClick.text = getString(R.string.youHaventFavAnyAyah)
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
                    tempMsApi1?.let {
                        syncNotifiedPrayer(it)
                        return@withContext
                    }
                }
                break
            }

            withContext(Dispatchers.Main){
                if(binding.tvWidgetPrayerCountdown != null){
                    val strHour = if(tempHour <= 9) "0$tempHour" else tempHour
                    val strMinute = if(tempMinute <= 9) "0$tempMinute" else tempMinute
                    val strSecond = if(tempSecond <= 9) "0$tempSecond" else tempSecond
                    binding.tvWidgetPrayerCountdown.text = "$strHour : $strMinute : $strSecond remaining"
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
        binding.includeQuranQuote.tvQuranAyahQuoteClick.setOnClickListener {
            it.visibility = View.GONE
            binding.includeQuranQuote.tvQuranAyahQuote.visibility = View.VISIBLE
        }
        binding.includeQuranQuote.tvQuranAyahQuote.setOnClickListener {
            binding.includeQuranQuote.tvQuranAyahQuoteClick.visibility = View.VISIBLE
            it.visibility = View.GONE
        }
    }

    private fun openPopupQuote(){
        binding.includeQuranQuote.ivQuoteSetting.setOnClickListener {

            val dialog = Dialog(requireContext())
            val dialogBinding = DataBindingUtil.inflate<LayoutPopupChooseQuoteSettingBinding>(
                layoutInflater, R.layout.layout_popup_choose_quote_setting, null, true
            )
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.setContentView(dialogBinding.root)
            dialog.show()

            viewModel.msSetting.observe(viewLifecycleOwner, {
                when(it.status){
                    EnumStatus.SUCCESS -> {
                        if(it.data != null){
                            if(it.data.isUsingDBQuotes)
                                dialogBinding.rbgQuotesDataSource.check(R.id.rb_fromFavQuote)
                            else
                                dialogBinding.rbgQuotesDataSource.check(R.id.rb_fromApi)
                        }
                    }
                    else -> {}
                }
            })

            dialogBinding.rbFromApi.setOnClickListener {
                viewModel.updateIsUsingDBQuotes(false)
                dismissDialog(dialog)
            }

            dialogBinding.rbFromFavQuote.setOnClickListener {
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
