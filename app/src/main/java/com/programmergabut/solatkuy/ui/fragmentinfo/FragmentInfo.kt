package com.programmergabut.solatkuy.ui.fragmentinfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Data
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerResponse
import com.programmergabut.solatkuy.ui.fragmentinfo.adapter.DuaCollectionAdapter
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
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
class FragmentInfo : Fragment(R.layout.fragment_info), SwipeRefreshLayout.OnRefreshListener {

    private val fragmentInfoViewModel: FragmentInfoViewModel by viewModels()

    private lateinit var duaCollectionAdapter: DuaCollectionAdapter
    private var mMsApi1: MsApi1? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObserversDB()

        refreshLayout()
        subscribeObserversAPI()
        initRvDuaCollection()
    }

    private fun initRvDuaCollection() {
        duaCollectionAdapter = DuaCollectionAdapter(requireContext())
        duaCollectionAdapter.setData(DuaGenerator.getListDua())

        rvDuaCollection.apply {
            adapter = duaCollectionAdapter
            layoutManager = LinearLayoutManager(this@FragmentInfo.context)
            setHasFixedSize(true)
        }
    }


    /* Subscribe live data */
    private fun subscribeObserversDB() {
        fragmentInfoViewModel.msApi1.observe(viewLifecycleOwner, Observer { retval ->
            when(retval.status){
                EnumStatus.SUCCESS -> {
                    mMsApi1 = retval.data

                    retval.data?.let {
                        val city = LocationHelper.getCity(requireContext(), it.latitude.toDouble(), it.longitude.toDouble())

                        tv_city.text = city ?: EnumConfig.lCity

                        fetchPrayerApi(it)
                    }


                }
                EnumStatus.LOADING -> {}
                EnumStatus.ERROR -> {}
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeObserversAPI(){

        /* fragmentInfoViewModel.asmaAlHusnaApi.observe(this, Observer {
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

        fragmentInfoViewModel.prayer.observe(viewLifecycleOwner, Observer {

            when(it.status){
                EnumStatus.SUCCESS -> {

                    val sdf = SimpleDateFormat("dd", Locale.getDefault())
                    val currentDate = sdf.format(Date())
                    val data = createTodayData(it.data!!, currentDate)
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
            }
        })

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
        fragmentInfoViewModel.fetchAsmaAlHusna(mMsApi1)
    }*/

    private fun fetchPrayerApi(mMsApi1: MsApi1) {
        fragmentInfoViewModel.fetchPrayerApi(mMsApi1)
    }

    /* Refresher */
    private fun refreshLayout() {
        sl_info.setOnRefreshListener(this)
    }

    override fun onRefresh() {

        if(mMsApi1 == null)
            throw Exception("null mMsApi1")

        fetchPrayerApi(mMsApi1!!)

        sl_info.isRefreshing = false
    }

}