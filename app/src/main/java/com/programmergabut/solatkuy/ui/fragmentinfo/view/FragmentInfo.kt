package com.programmergabut.solatkuy.ui.fragmentinfo.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.data.model.prayerJson.Data
import com.programmergabut.solatkuy.data.model.prayerJson.PrayerApi
import com.programmergabut.solatkuy.ui.fragmentinfo.viewmodel.FragmentInfoViewModel
import com.programmergabut.solatkuy.ui.fragmentinfo.adapter.FragmentInfoAdapter
import com.programmergabut.solatkuy.util.EnumConfig
import com.programmergabut.solatkuy.util.EnumStatus
import com.programmergabut.solatkuy.util.LocationHelper
import com.programmergabut.solatkuy.util.Resource
import kotlinx.android.synthetic.main.fragment_info.*
import org.joda.time.LocalDate
import java.text.SimpleDateFormat
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 24/04/20.
 */

class FragmentInfo : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var fragmentInfoViewModel: FragmentInfoViewModel
    private var mMsApi1: MsApi1? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentInfoViewModel = ViewModelProviders.of(this).get(FragmentInfoViewModel::class.java)

        subscribeObserversDB()
        fetchAsmaAlHusnaApi()

        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshLayout()
        subscribeObserversAPI()
    }


    /* Subscribe live data */
    private fun subscribeObserversDB() {
        val currDate = LocalDate()

        fragmentInfoViewModel.msApi1Local.observe(this, Observer {
            mMsApi1 = it

            val city = LocationHelper.getCity(context!!, it.latitude.toDouble(), it.longitude.toDouble())

            tv_city.text = city ?: EnumConfig.lCity

            fetchPrayerApi(it.latitude, it.longitude, EnumConfig.pMethod, currDate.monthOfYear.toString(),currDate.year.toString())
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
                    val data = createOnlineData(it.data!!, currentDate)
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
                EnumStatus.ERROR -> {
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

    private fun initAHAdapter(datas: List<com.programmergabut.solatkuy.data.model.asmaalhusnaJson.Data>) {

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
    private fun createOnlineData(it: PrayerApi?, currentDate: String): Data? {
        return it?.data?.find { obj -> obj.date.gregorian.day == currentDate }
    }

    /* Fetch API Data */
    private fun fetchPrayerApi(latitude: String, longitude: String, method: String, month: String, year: String) {
        fragmentInfoViewModel.fetchPrayerApi(latitude,longitude,method, month,year)
    }

    private fun fetchAsmaAlHusnaApi() {
        fragmentInfoViewModel.fetchAsmaAlHusnaApi()
    }

    /* Refresher */
    private fun refreshLayout() {
        sl_info.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        val currDate = LocalDate()

        //fetch Prayer Api
        fragmentInfoViewModel.prayerApi.postValue(Resource.loading(null))
        if(mMsApi1 != null)
            fetchPrayerApi(mMsApi1?.latitude!!, mMsApi1?.longitude!!, EnumConfig.pMethod, currDate.monthOfYear.toString(),currDate.year.toString())
        else
            fragmentInfoViewModel.prayerApi.postValue(Resource.error(getString(R.string.fetch_failed), null))

        //fetch Asma Al Husna
        fragmentInfoViewModel.asmaAlHusnaApi.postValue(Resource.loading(null))
        fetchAsmaAlHusnaApi()

        sl_info.isRefreshing = false
    }

}
