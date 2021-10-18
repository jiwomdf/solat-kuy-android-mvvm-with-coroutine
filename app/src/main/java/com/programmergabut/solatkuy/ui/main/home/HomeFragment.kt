package com.programmergabut.solatkuy.ui.main.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseFragment
import com.programmergabut.solatkuy.data.hardcodedata.DuaData
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.local.localentity.MsNotifiedPrayer
import com.programmergabut.solatkuy.data.remote.json.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.data.remote.json.prayerJson.Result
import com.programmergabut.solatkuy.data.remote.json.readsurahJsonEn.ReadSurahEnResponse
import com.programmergabut.solatkuy.databinding.*
import com.programmergabut.solatkuy.model.Timings
import com.programmergabut.solatkuy.ui.LocationHelper
import com.programmergabut.solatkuy.ui.main.SelectPrayerHelper
import com.programmergabut.solatkuy.util.Constant
import com.programmergabut.solatkuy.util.EnumStatus
import com.programmergabut.solatkuy.worker.FireAlarmManagerWorker
import com.programmergabut.solatkuy.worker.UpdateMonthAndYearWorker
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.*
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.Period
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class HomeFragment(
    viewModelTestFragment: HomeViewModel? = null
) : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    R.layout.fragment_home,
    HomeViewModel::class, viewModelTestFragment
), View.OnClickListener {

    private val TAG = "HomeFragment"

    private var isTimerHasBanded = false
    private var coroutineTimerJob: Job? = null
    private lateinit var duaCollectionAdapter: DuaCollectionAdapter

    override fun getViewBinding() = FragmentHomeBinding.inflate(layoutInflater)

    override fun onPause() {
        super.onPause()
        isTimerHasBanded = false
        coroutineTimerJob?.cancel()
    }

    override fun onStart() {
        super.onStart()
        binding.tvWidgetPrayerCountdown.text = getString(R.string.loading)
        binding.includeQuranQuote?.tvQuranAyahQuote.visibility = View.GONE
        binding.includeQuranQuote?.tvQuranAyahQuoteClick.visibility = View.VISIBLE

        viewModel.notifiedPrayer.value?.data?.let {
            val timing = createWidgetData(it)
            val selectedPrayer = SelectPrayerHelper.selectNextPrayer(timing)
            selectNextPrayerTime(selectedPrayer, timing)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRvDuaCollection()
        fireUpdateMonthYearWorker()
    }

    override fun setListener() {
        super.setListener()
        cbClickListener()
        binding.includeQuranQuote.tvQuranAyahQuoteClick.setOnClickListener(this)
        binding.includeQuranQuote.tvQuranAyahQuote.setOnClickListener(this)
        binding.includeQuranQuote.ivRefresh.setOnClickListener(this)
        subscribeObserversDB()
        subscribeObserversAPI()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_quran_ayah_quote_click -> {
                v.visibility = View.GONE
                binding.includeQuranQuote.tvQuranAyahQuote.visibility = View.VISIBLE
            }
            R.id.tv_quran_ayah_quote -> {
                binding.includeQuranQuote.tvQuranAyahQuoteClick.visibility = View.VISIBLE
                v.visibility = View.GONE
            }
            R.id.iv_refresh -> {
                viewModel.getMsSetting()
            }
        }
    }

    private fun initRvDuaCollection() {
        duaCollectionAdapter = DuaCollectionAdapter(requireContext())
        duaCollectionAdapter.setData(DuaData.getListDua())
        binding.includeInfo.rvDuaCollection.apply {
            adapter = duaCollectionAdapter
            layoutManager = LinearLayoutManager(this@HomeFragment.context)
            setHasFixedSize(true)
        }
    }

    private fun subscribeObserversAPI() {
        viewModel.notifiedPrayer.observe(viewLifecycleOwner, { retVal ->
            when (retVal.status) {
                EnumStatus.SUCCESS, EnumStatus.ERROR -> {
                    if (retVal.data == null) {
                        showBottomSheet(isCancelable = false, isFinish = true)
                        return@observe
                    }
                    if (retVal.data.isEmpty())
                        return@observe

                    bindCheckBox(retVal.data)
                    fireWorker()
                    val widget = createWidgetData(retVal.data)
                    bindWidget(widget)
                }
                EnumStatus.LOADING -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.syncdata),
                        Toast.LENGTH_SHORT
                    ).show()
                    bindPrayerText(null)
                }
            }
        })

        viewModel.prayer.observe(viewLifecycleOwner, {
            when (it.status) {
                EnumStatus.SUCCESS -> {
                    val data = createTodayData(it.data)
                    val date = data?.date
                    val hijriDate = date?.hijri
                    val gregorianDate = date?.gregorian

                    binding.includeInfo.tvImsakDate.text = date?.readable
                    binding.includeInfo.tvImsakTime.text = data?.timings?.imsak
                    binding.includeInfo.tvGregorianDate.text = gregorianDate?.date
                    binding.includeInfo.tvHijriDate.text = hijriDate?.date
                    binding.includeInfo.tvGregorianMonth.text = gregorianDate?.month?.en
                    binding.includeInfo.tvHijriMonth.text =
                        hijriDate?.month?.en + " / " + hijriDate?.month?.ar
                    binding.includeInfo.tvGregorianDay.text = gregorianDate?.weekday?.en
                    binding.includeInfo.tvHijriDay.text =
                        hijriDate?.weekday?.en + " / " + hijriDate?.weekday?.ar
                }
                EnumStatus.LOADING -> setState(it.status)
                EnumStatus.ERROR -> setState(it.status)
            }
        })

        viewModel.readSurahEn.observe(viewLifecycleOwner, { apiQuotes ->
            when (apiQuotes.status) {
                EnumStatus.SUCCESS -> {
                    if (apiQuotes.data == null)
                        return@observe

                    bindQuranQuoteApiOnline(apiQuotes.data)
                }
                EnumStatus.LOADING -> {
                    binding.includeQuranQuote.tvQuranAyahQuote.text = getString(R.string.loading)
                    binding.includeQuranQuote.tvQuranAyahQuoteClick.text =
                        getString(R.string.loading)
                }
                EnumStatus.ERROR -> {
                    binding.includeQuranQuote.tvQuranAyahQuote.text =
                        getString(R.string.fetch_failed)
                    binding.includeQuranQuote.tvQuranAyahQuoteClick.text =
                        getString(R.string.fetch_failed)
                }
            }
        })
    }

    private fun subscribeObserversDB() {
        viewModel.msApi1.observe(viewLifecycleOwner, { api1 ->
            if (api1 == null)
                return@observe

            bindWidgetLocation(api1)
            updateMonthAndYearMsApi1(api1)
            viewModel.getListNotifiedPrayer(api1)

            val city = LocationHelper.getCity(
                requireContext(),
                api1.latitude.toDouble(),
                api1.longitude.toDouble()
            )
            binding.includeInfo.tvCity.text = city ?: Constant.CITY_NOT_FOUND
            viewModel.fetchPrayerApi(api1)
        })

        viewModel.msSetting.observe(viewLifecycleOwner, { setting ->
            if (setting == null) {
                viewModel.fetchReadSurahEn((Constant.STARTED_SURAH..Constant.ENDED_SURAH).random())
                return@observe
            }

            viewModel.fetchReadSurahEn((Constant.STARTED_SURAH..Constant.ENDED_SURAH).random())
        })
    }

    private fun setState(status: EnumStatus) {
        when (status) {
            EnumStatus.SUCCESS -> {/*NO-OP*/
            }
            EnumStatus.LOADING -> {
                binding.includeInfo.apply {
                    tvImsakDate.text = getString(R.string.loading)
                    tvImsakTime.text = getString(R.string.loading)
                    tvGregorianDate.text = getString(R.string.loading)
                    tvHijriDate.text = getString(R.string.loading)
                    tvGregorianMonth.text = getString(R.string.loading)
                    tvHijriMonth.text = getString(R.string.loading)
                    tvGregorianDay.text = getString(R.string.loading)
                    tvHijriDay.text = getString(R.string.loading)
                }
            }
            EnumStatus.ERROR -> {
                binding.includeInfo.apply {
                    tvImsakDate.text = getString(R.string.fetch_failed)
                    tvImsakTime.text = getString(R.string.fetch_failed_na)
                    tvGregorianDate.text = getString(R.string.fetch_failed_na)
                    tvHijriDate.text = getString(R.string.fetch_failed_na)
                    tvGregorianMonth.text = getString(R.string.fetch_failed_na)
                    tvHijriMonth.text = getString(R.string.fetch_failed_na)
                    tvGregorianDay.text = getString(R.string.fetch_failed_na)
                    tvHijriDay.text = getString(R.string.fetch_failed_na)
                }
            }
        }
    }

    private fun createTodayData(it: PrayerResponse?): Result? = it?.data?.find { obj ->
        obj.date.gregorian?.day == SimpleDateFormat("dd", Locale.getDefault()).format(Date())
    }

    private fun updateMonthAndYearMsApi1(data: MsApi1) {
        val arrDate = LocalDate.now().toString("dd/M/yyyy").split("/")
        val year = arrDate[2]
        val month = arrDate[1]
        val dbYear = data.year.toInt()
        val dbMoth = data.month.toInt()

        if (year.toInt() > dbYear && month.toInt() > dbMoth) {
            viewModel.updateMsApi1MonthAndYear(1, arrDate[1], arrDate[2])
        }
    }

    private fun createWidgetData(prayerMs: List<MsNotifiedPrayer>): Timings {
        val arrDate = LocalDate.now().toString("dd/MMM/yyyy").split("/")
        return Timings(
            prayerMs[0].prayerTime, prayerMs[1].prayerTime, prayerMs[2].prayerTime,
            prayerMs[3].prayerTime, prayerMs[4].prayerTime, prayerMs[5].prayerTime, "",
            "", "", arrDate[0], arrDate[1]
        )
    }

    private fun updatePrayerIsNotified(prayer: String, isNotified: Boolean) {
        if (isNotified)
            Toasty.success(
                requireContext(),
                "$prayer will be notified every day",
                Toast.LENGTH_SHORT
            ).show()
        else
            Toasty.warning(
                requireContext(),
                "$prayer will not be notified anymore",
                Toast.LENGTH_SHORT
            ).show()

        viewModel.updatePrayerIsNotified(prayer, isNotified)
    }

    /* Widget */
    private fun cbClickListener() {
        binding.includePrayerTime.cbFajr.setOnClickListener {
            if (binding.includePrayerTime.cbFajr.isChecked)
                updatePrayerIsNotified(getString(R.string.fajr), true)
            else
                updatePrayerIsNotified(getString(R.string.fajr), false)
        }
        binding.includePrayerTime.cbDhuhr.setOnClickListener {
            if (binding.includePrayerTime.cbDhuhr.isChecked)
                updatePrayerIsNotified(getString(R.string.dhuhr), true)
            else
                updatePrayerIsNotified(getString(R.string.dhuhr), false)
        }
        binding.includePrayerTime.cbAsr.setOnClickListener {
            if (binding.includePrayerTime.cbAsr.isChecked)
                updatePrayerIsNotified(getString(R.string.asr), true)
            else
                updatePrayerIsNotified(getString(R.string.asr), false)
        }
        binding.includePrayerTime.cbMaghrib.setOnClickListener {
            if (binding.includePrayerTime.cbMaghrib.isChecked)
                updatePrayerIsNotified(getString(R.string.maghrib), true)
            else
                updatePrayerIsNotified(getString(R.string.maghrib), false)
        }
        binding.includePrayerTime.cbIsha.setOnClickListener {
            if (binding.includePrayerTime.cbIsha.isChecked)
                updatePrayerIsNotified(getString(R.string.isha), true)
            else
                updatePrayerIsNotified(getString(R.string.isha), false)
        }
    }

    private fun bindCheckBox(list: List<MsNotifiedPrayer>) {
        list.forEach {
            when {
                it.prayerName.trim() == getString(R.string.fajr) && it.isNotified -> binding.includePrayerTime.cbFajr.isChecked =
                    true
                it.prayerName.trim() == getString(R.string.dhuhr) && it.isNotified -> binding.includePrayerTime.cbDhuhr.isChecked =
                    true
                it.prayerName.trim() == getString(R.string.asr) && it.isNotified -> binding.includePrayerTime.cbAsr.isChecked =
                    true
                it.prayerName.trim() == getString(R.string.maghrib) && it.isNotified -> binding.includePrayerTime.cbMaghrib.isChecked =
                    true
                it.prayerName.trim() == getString(R.string.isha) && it.isNotified -> binding.includePrayerTime.cbIsha.isChecked =
                    true
            }
        }
    }

    private fun bindWidget(data: Timings?) {
        if (data == null)
            return

        val selectedPrayer = SelectPrayerHelper.selectNextPrayer(data)
        bindPrayerText(data)
        selectWidgetTitle(selectedPrayer)
        selectWidgetPic(selectedPrayer)
        selectNextPrayerTime(selectedPrayer, data)
    }

    private fun bindWidgetLocation(it: MsApi1) {
        val city = LocationHelper.getCity(
            requireContext(),
            it.latitude.toDouble(),
            it.longitude.toDouble()
        )
        binding.tvViewLatitude.text = it.latitude + " °N"
        binding.tvViewLongitude.text = it.longitude + " °W"
        binding.tvViewCity.text = city ?: Constant.CITY_NOT_FOUND
    }

    private fun bindPrayerText(apiData: Timings?) {
        if (apiData == null) {
            binding.includePrayerTime.apply {
                tvFajrTime.text = getString(R.string.loading)
                tvDhuhrTime.text = getString(R.string.loading)
                tvAsrTime.text = getString(R.string.loading)
                tvMaghribTime.text = getString(R.string.loading)
                tvIshaTime.text = getString(R.string.loading)
                tvDateChange.text = getString(R.string.loading)
            }
        } else {
            binding.includePrayerTime.apply {
                tvFajrTime.text = apiData.fajr
                tvDhuhrTime.text = apiData.dhuhr
                tvAsrTime.text = apiData.asr
                tvMaghribTime.text = apiData.maghrib
                tvIshaTime.text = apiData.isha
                tvDateChange.text = "${apiData.month} ${apiData.day} "
            }
        }
    }

    private fun selectNextPrayerTime(selectedPrayer: Int, timings: Timings) {
        val sdfPrayer = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val nowTime = DateTime(sdfPrayer.parse(org.joda.time.LocalTime.now().toString()))
        var period: Period? = null

        when (selectedPrayer) {
            -1 -> period = Period(
                nowTime,
                DateTime(sdfPrayer.parse(timings.dhuhr.split(" ")[0].trim() + ":00"))
            )
            1 -> period = Period(
                nowTime,
                DateTime(sdfPrayer.parse(timings.sunrise.split(" ")[0].trim() + ":00"))
            )
            2 -> period =
                Period(nowTime, DateTime(sdfPrayer.parse(timings.asr.split(" ")[0].trim() + ":00")))
            3 -> period = Period(
                nowTime,
                DateTime(sdfPrayer.parse(timings.maghrib.split(" ")[0].trim() + ":00"))
            )
            4 -> period = Period(
                nowTime,
                DateTime(sdfPrayer.parse(timings.isha.split(" ")[0].trim() + ":00"))
            )
            5 -> period = Period(
                nowTime,
                DateTime(sdfPrayer.parse(timings.fajr.split(" ")[0].trim() + ":00")).plusDays(1)
            )
            6 -> period = Period(
                nowTime,
                DateTime(sdfPrayer.parse(timings.fajr.split(" ")[0].trim() + ":00"))
            )
        }

        if (period == null)
            return

        if (!isTimerHasBanded) {
            coroutineTimerJob = lifecycleScope.launch(Dispatchers.IO) {
                coroutineTimer(this, period.hours, period.minutes, 60 - nowTime.secondOfMinute)
            }
            isTimerHasBanded = true
        }
    }

    private fun selectWidgetTitle(selectedPrayer: Int) {
        when (selectedPrayer) {
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
        when (selectedPrayer) {
            -1 -> widgetDrawable =
                getDrawable(context?.applicationContext!!, R.drawable.img_sunrise)
            1 -> widgetDrawable = getDrawable(context?.applicationContext!!, R.drawable.img_fajr)
            2 -> widgetDrawable = getDrawable(context?.applicationContext!!, R.drawable.img_dhuhr)
            3 -> widgetDrawable = getDrawable(context?.applicationContext!!, R.drawable.img_asr)
            4 -> widgetDrawable = getDrawable(context?.applicationContext!!, R.drawable.img_maghrib)
            5 -> widgetDrawable = getDrawable(context?.applicationContext!!, R.drawable.img_isha)
            6 -> widgetDrawable = getDrawable(context?.applicationContext!!, R.drawable.img_isha)
        }

        if (widgetDrawable != null) {
            val crossFadeFactory =
                DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
            Glide.with(this)
                .load(widgetDrawable)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .transition(DrawableTransitionOptions.withCrossFade(crossFadeFactory))
                .into(binding.ivPrayerWidget)
        }
    }

    private fun bindQuranQuoteApiOnline(retVal: ReadSurahEnResponse) {
        val returnValue = retVal.data
        val randAyah = (returnValue.ayahs.indices).random()
        val ayah =
            returnValue.ayahs[randAyah].text + " - QS " + returnValue.englishName + " Ayah " + returnValue.numberOfAyahs
        binding.includeQuranQuote.tvQuranAyahQuote.text =
            if (ayah.length > 100) ayah.substring(0, 100) + "..." else ayah
        binding.includeQuranQuote.tvQuranAyahQuoteClick.text = ayah
    }

    /* Coroutine Timer */
    private suspend fun coroutineTimer(scope: CoroutineScope, hour: Int, minute: Int, second: Int) {
        var tempHour = abs(hour)
        var tempMinute = abs(minute)
        var tempSecond = abs(second)
        var isMinuteZero = false

        while (true) {
            if (!scope.isActive) {
                coroutineTimerJob?.cancel()
                break
            }

            delay(1000)
            tempSecond--

            if (tempSecond == 0) {
                tempSecond = 60
                if (tempMinute != 0)
                    tempMinute -= 1
                if (tempMinute == 0)
                    isMinuteZero = true
            }

            if (tempMinute == 0) {
                if (!isMinuteZero)
                    tempMinute = 59
                if (tempHour != 0)
                    tempHour -= 1
            }

            withContext(Dispatchers.Main) {
                if (binding.tvWidgetPrayerCountdown != null) {
                    val strHour = if (tempHour <= 9) "0$tempHour" else tempHour
                    val strMinute = if (tempMinute <= 9) "0$tempMinute" else tempMinute
                    val strSecond = if (tempSecond <= 9) "0$tempSecond" else tempSecond
                    binding.tvWidgetPrayerCountdown.text =
                        "$strHour : $strMinute : $strSecond remaining"
                } else {
                    return@withContext
                }
            }

            /* fetching Prayer API */
            if (tempHour == 0 && tempMinute == 0 && tempSecond == 1) {
                withContext(Dispatchers.Main) {
                    viewModel.msApi1.value?.let {
                        viewModel.getListNotifiedPrayer(it)
                        isTimerHasBanded = false
                    }
                }
                break
            }
        }
    }

    private fun fireWorker() {
        val task =
            PeriodicWorkRequest.Builder(FireAlarmManagerWorker::class.java, 60, TimeUnit.MINUTES)
                .build()
        val workManager = WorkManager.getInstance(requireActivity().application)
        workManager.enqueueUniquePeriodicWork(
            FireAlarmManagerWorker.UNIQUE_KEY,
            ExistingPeriodicWorkPolicy.KEEP,
            task
        )
    }

    private fun fireUpdateMonthYearWorker() {
        val task =
            PeriodicWorkRequest.Builder(UpdateMonthAndYearWorker::class.java, 720, TimeUnit.MINUTES)
                .build()
        val workManager = WorkManager.getInstance(requireActivity().application)
        workManager.enqueueUniquePeriodicWork(
            UpdateMonthAndYearWorker.UNIQUE_KEY,
            ExistingPeriodicWorkPolicy.KEEP,
            task
        )
    }

}
