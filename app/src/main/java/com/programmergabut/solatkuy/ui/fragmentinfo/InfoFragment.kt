package com.programmergabut.solatkuy.ui.fragmentinfo

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseFragment
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Data
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.ui.fragmentcompass.FragmentCompassViewModel
import com.programmergabut.solatkuy.ui.fragmentinfo.adapter.DuaCollectionAdapter
import com.programmergabut.solatkuy.util.EnumStatus
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.util.EnumConfig
import com.programmergabut.solatkuy.util.EnumConfig.Companion.IS_TESTING
import com.programmergabut.solatkuy.util.generator.DuaGenerator
import com.programmergabut.solatkuy.util.helper.LocationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_info.*
import java.text.SimpleDateFormat
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 24/04/20.
 */

@AndroidEntryPoint
class InfoFragment(viewModelTest: FragmentInfoViewModel? = null) : BaseFragment<FragmentInfoViewModel>(
    R.layout.fragment_info, FragmentInfoViewModel::class.java, viewModelTest
), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var duaCollectionAdapter: DuaCollectionAdapter
    private var mMsApi1: MsApi1? = null

    override fun setFirstView() {
        initRvDuaCollection()
    }
    override fun setObserver() {
        observeDB()
        observeAPI()
    }
    override fun setListener() {
        sl_info.setOnRefreshListener(this)
    }

    private fun initRvDuaCollection() {
        duaCollectionAdapter = DuaCollectionAdapter(requireContext())
        duaCollectionAdapter.setData(DuaGenerator.getListDua())

        rvDuaCollection.apply {
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

                    tv_city.text = city ?: EnumConfig.CITY_NOT_FOUND_STR
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

        viewModel.prayerStatus.observe(viewLifecycleOwner, {

            when(it.status){
                EnumStatus.SUCCESS -> {
                    val sdf = SimpleDateFormat("dd", Locale.getDefault())
                    val currentDate = sdf.format(Date())
                    val data = createTodayData(viewModel.prayer, currentDate)
                    val date = data?.date
                    val hijriDate = date?.hijri
                    val gregorianDate = date?.gregorian

                    tv_imsak_date.text = date?.readable
                    tv_imsak_time.text = data?.timings?.imsak

                    tv_gregorian_date.text = gregorianDate?.date
                    tv_hijri_date.text = hijriDate?.date
                    tv_gregorian_month.text = gregorianDate?.month?.en
                    tv_hijri_month.text = hijriDate?.month?.en + " / " + hijriDate?.month?.ar
                    tv_gregorian_day.text = gregorianDate?.weekday?.en
                    tv_hijri_day.text = hijriDate?.weekday?.en + " / " + hijriDate?.weekday?.ar
                }
                EnumStatus.LOADING -> {
                    setState(it.status)
                }
                EnumStatus.ERROR ->{
                    showBottomSheet(description = getString(R.string.fetch_failed), isCancelable = true, isFinish = false)
                    setState(it.status)
                }
                else -> {/* NO-OP */}
            }.also {
                viewModel.prayerStatus.postValue(Resource.neutral())
            }
        })

    }

    private fun setState(status: EnumStatus){
        when(status){
            EnumStatus.SUCCESS -> { }
            EnumStatus.LOADING -> {
                tv_imsak_date.text = getString(R.string.loading)
                tv_imsak_time.text = getString(R.string.loading)
                tv_gregorian_date.text = getString(R.string.loading)
                tv_hijri_date.text = getString(R.string.loading)
                tv_gregorian_month.text = getString(R.string.loading)
                tv_hijri_month.text = getString(R.string.loading)
                tv_gregorian_day.text = getString(R.string.loading)
                tv_hijri_day.text = getString(R.string.loading)
            }
            EnumStatus.ERROR ->{
                tv_imsak_date.text = getString(R.string.fetch_failed)
                tv_imsak_time.text = getString(R.string.fetch_failed_sort)
                tv_gregorian_date.text = getString(R.string.fetch_failed_sort)
                tv_hijri_date.text = getString(R.string.fetch_failed_sort)
                tv_gregorian_month.text = getString(R.string.fetch_failed_sort)
                tv_hijri_month.text = getString(R.string.fetch_failed_sort)
                tv_gregorian_day.text = getString(R.string.fetch_failed_sort)
                tv_hijri_day.text = getString(R.string.fetch_failed_sort)
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
        sl_info.isRefreshing = false
    }

}
