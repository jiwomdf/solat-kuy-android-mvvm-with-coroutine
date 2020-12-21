package com.programmergabut.solatkuy.ui.fragmentinfo

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseFragment
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Data
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.databinding.FragmentInfoBinding
import com.programmergabut.solatkuy.ui.fragmentinfo.adapter.DuaCollectionAdapter
import com.programmergabut.solatkuy.util.EnumConfig
import com.programmergabut.solatkuy.util.EnumStatus
import com.programmergabut.solatkuy.util.generator.DuaGenerator
import com.programmergabut.solatkuy.util.helper.LocationHelper
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 24/04/20.
 */

@AndroidEntryPoint
class InfoFragment(viewModelTest: FragmentInfoViewModel? = null) : BaseFragment<FragmentInfoBinding, FragmentInfoViewModel>(
    R.layout.fragment_info, FragmentInfoViewModel::class.java, viewModelTest
), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var duaCollectionAdapter: DuaCollectionAdapter
    private var mMsApi1: MsApi1? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRvDuaCollection()
        observeDB()
        observeAPI()
    }

    override fun setListener() {
        super.setListener()
        binding.slInfo.setOnRefreshListener(this)
    }

    private fun initRvDuaCollection() {
        duaCollectionAdapter = DuaCollectionAdapter(requireContext())
        duaCollectionAdapter.setData(DuaGenerator.getListDua())

        binding.rvDuaCollection.apply {
            adapter = duaCollectionAdapter
            layoutManager = LinearLayoutManager(this@InfoFragment.context)
            setHasFixedSize(true)
        }
    }

    /* Subscribe live data */
    private fun observeDB() {
        viewModel.msApi1.observe(viewLifecycleOwner, { retval ->
            when(retval.status){
                EnumStatus.SUCCESS -> {
                    if(retval.data == null)
                        showBottomSheet(isCancelable = false, isFinish = true)

                    mMsApi1 = retval.data!!
                    val city = LocationHelper.getCity(requireContext(), retval.data.latitude.toDouble(), retval.data.longitude.toDouble())

                    binding.tvCity.text = city ?: EnumConfig.CITY_NOT_FOUND_STR
                    fetchPrayerApi(retval.data)
                }
                EnumStatus.ERROR -> showBottomSheet(isCancelable = false, isFinish = true)
                else -> {/*NO-OP*/}
            }
        })
    }

    private fun observeAPI(){

        /* viewModel.asmaAlHusnaApi.observe(this, Observer {
            when(it.Status) {
                EnumStatus.SUCCESS -> {
                    tv_ah_loading.visibility = View.GONE
                    rv_ah.visibility = View.VISIBLE
                    initAHAdapter(it?.data?.data!!)
                }
                EnumStatus.LOADING ->{
                    tv_ah_loading.visibility = View.VISIBLE
                    rv_ah.visibility = View.GONE
                    tv_ah_loading.text = getString(R.string.loading)
                }
                EnumStatus.ERROR -> {
                    tv_ah_loading.visibility = View.VISIBLE
                    rv_ah.visibility = View.GONE
                    tv_ah_loading.text = getString(R.string.fetch_failed)
                }
            }
        }) */

        viewModel.prayer.observe(viewLifecycleOwner, {

            when(it.status){
                EnumStatus.SUCCESS -> {
                    val sdf = SimpleDateFormat("dd", Locale.getDefault())
                    val currentDate = sdf.format(Date())
                    val data = createTodayData(it.data, currentDate)
                    val date = data?.date
                    val hijriDate = date?.hijri
                    val gregorianDate = date?.gregorian

                    binding.tvImsakDate.text = date?.readable
                    binding.tvImsakTime.text = data?.timings?.imsak
                    binding.tvGregorianDate.text = gregorianDate?.date
                    binding.tvHijriDate.text = hijriDate?.date
                    binding.tvGregorianMonth.text = gregorianDate?.month?.en
                    binding.tvHijriMonth.text = hijriDate?.month?.en + " / " + hijriDate?.month?.ar
                    binding.tvGregorianDay.text = gregorianDate?.weekday?.en
                    binding.tvHijriDay.text = hijriDate?.weekday?.en + " / " + hijriDate?.weekday?.ar
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

    }

    private fun setState(status: EnumStatus){
        when(status){
            EnumStatus.SUCCESS -> { }
            EnumStatus.LOADING -> {
                binding.tvImsakDate.text = getString(R.string.loading)
                binding.tvImsakTime.text = getString(R.string.loading)
                binding.tvGregorianDate.text = getString(R.string.loading)
                binding.tvHijriDate.text = getString(R.string.loading)
                binding.tvGregorianMonth.text = getString(R.string.loading)
                binding.tvHijriMonth.text = getString(R.string.loading)
                binding.tvGregorianDay.text = getString(R.string.loading)
                binding.tvHijriDay.text = getString(R.string.loading)
            }
            EnumStatus.ERROR ->{
                binding.tvImsakDate.text = getString(R.string.fetch_failed)
                binding.tvImsakTime.text = getString(R.string.fetch_failed_sort)
                binding.tvGregorianDate.text = getString(R.string.fetch_failed_sort)
                binding.tvHijriDate.text = getString(R.string.fetch_failed_sort)
                binding.tvGregorianMonth.text = getString(R.string.fetch_failed_sort)
                binding.tvHijriMonth.text = getString(R.string.fetch_failed_sort)
                binding.tvGregorianDay.text = getString(R.string.fetch_failed_sort)
                binding.tvHijriDay.text = getString(R.string.fetch_failed_sort)
            }
            else -> {/*NO-OP*/}
        }
    }

    /* private fun initAHAdapter(datas: List<com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.Data>) {

        rv_ah.apply {
            adapter =
                FragmentInfoAdapter(
                    datas
                )
            layoutManager = LinearLayoutManager(this@FragmentInfo.context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

    } */


    /* Create data from API */
    private fun createTodayData(it: PrayerResponse?, currentDate: String): Data? {
        return it?.data?.find { obj -> obj.date.gregorian?.day == currentDate }
    }

    /* Fetch API Data */
    /* private fun fetchAsmaAlHusnaApi(mMsApi1: MsApi1) {
        viewModel.fetchAsmaAlHusna(mMsApi1)
    }*/

    private fun fetchPrayerApi(mMsApi1: MsApi1) {
        viewModel.fetchPrayerApi(mMsApi1)
    }

    override fun onRefresh() {
        if(mMsApi1 == null)
            showBottomSheet(isCancelable = false, isFinish = true)

        fetchPrayerApi(mMsApi1!!)
        binding.slInfo.isRefreshing = false
    }

}
