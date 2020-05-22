package com.programmergabut.solatkuy.ui.fragmentinfo.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.Data
import com.programmergabut.solatkuy.data.remote.remoteentity.prayerJson.PrayerApi
import com.programmergabut.solatkuy.ui.activityprayer.DuaActivity
import com.programmergabut.solatkuy.ui.fragmentinfo.viewmodel.FragmentInfoViewModel
import com.programmergabut.solatkuy.ui.fragmentinfo.adapter.FragmentInfoAdapter
import com.programmergabut.solatkuy.util.*
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
import com.programmergabut.solatkuy.util.generator.DuaGenerator
import com.programmergabut.solatkuy.util.helper.LocationHelper
import com.programmergabut.solatkuy.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_info.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 24/04/20.
 */

class FragmentInfo : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var fragmentInfoViewModel: FragmentInfoViewModel
    private var mMsApi1: MsApi1? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentInfoViewModel = ViewModelProvider(this, ViewModelFactory
            .getInstance(activity?.application!!))[FragmentInfoViewModel::class.java]

        subscribeObserversDB()

        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshLayout()
        subscribeObserversAPI()
        openDua()
    }


    /* Subscribe live data */
    private fun subscribeObserversDB() {
        fragmentInfoViewModel.msApi1Local.observe(this, Observer {
            mMsApi1 = it

            val city = LocationHelper.getCity(context!!, it.latitude.toDouble(), it.longitude.toDouble())

            tv_city.text = city ?: EnumConfig.lCity

            fetchAsmaAlHusnaApi(it)
        })
    }

    private fun subscribeObserversAPI(){

        fragmentInfoViewModel.asmaAlHusnaApi.observe(this, Observer {
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
        })


        fragmentInfoViewModel.prayerApi.observe(this, Observer {

            when(it.Status){
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

    private fun initAHAdapter(datas: List<com.programmergabut.solatkuy.data.remote.remoteentity.asmaalhusnaJson.Data>) {

        rv_ah.apply {
            adapter =
                FragmentInfoAdapter(
                    datas
                )
            layoutManager = LinearLayoutManager(this@FragmentInfo.context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

    }


    /* Create data from API */
    private fun createTodayData(it: PrayerApi?, currentDate: String): Data? {
        return it?.data?.find { obj -> obj.date.gregorian.day == currentDate }
    }

    /* Fetch API Data */
    private fun fetchAsmaAlHusnaApi(mMsApi1: MsApi1) {
        fragmentInfoViewModel.asmaAlHusnaApi.postValue(Resource.loading(null))
        fragmentInfoViewModel.fetchAsmaAlHusna(mMsApi1)
    }

    private fun fetchPrayerApi(mMsApi1: MsApi1) {
        fragmentInfoViewModel.prayerApi.postValue(Resource.loading(null))
        fragmentInfoViewModel.fetchPrayerApi(mMsApi1)
    }

    /* Refresher */
    private fun refreshLayout() {
        sl_info.setOnRefreshListener(this)
    }

    override fun onRefresh() {

        if(mMsApi1 == null)
            throw Exception("null mMsApi1")

        //fetch Prayer Api
        fetchPrayerApi(mMsApi1!!)

        //fetch Asma Al Husna
        fetchAsmaAlHusnaApi(mMsApi1!!)

        sl_info.isRefreshing = false
    }

    /* Dua For After Adhan */
    private fun openDua(){
        val i = Intent(context, DuaActivity::class.java)

        btn_dua_after_adhan.setOnClickListener {

            i.putExtra(DuaActivity.duaTitle, DuaGenerator.duaAfterAdhan.title)
            i.putExtra(DuaActivity.duaAr, DuaGenerator.duaAfterAdhan.arab)
            i.putExtra(DuaActivity.duaLt, DuaGenerator.duaAfterAdhan.latin)
            i.putExtra(DuaActivity.duaEn, DuaGenerator.duaAfterAdhan.english)
            i.putExtra(DuaActivity.duaIn, DuaGenerator.duaAfterAdhan.indonesia)

            startActivity(i)
        }

        btn_dua_save_from_hell_world.setOnClickListener{
            i.putExtra(DuaActivity.duaTitle, DuaGenerator.duaSaveFromHell.title)
            i.putExtra(DuaActivity.duaAr, DuaGenerator.duaSaveFromHell.arab)
            i.putExtra(DuaActivity.duaLt, DuaGenerator.duaSaveFromHell.latin)
            i.putExtra(DuaActivity.duaEn, DuaGenerator.duaSaveFromHell.english)
            i.putExtra(DuaActivity.duaIn, DuaGenerator.duaSaveFromHell.indonesia)

            startActivity(i)
        }
    }

}
