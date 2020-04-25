package com.programmergabut.solatkuy.ui.fragmentinfo.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.data.model.prayerJson.Data
import com.programmergabut.solatkuy.data.model.prayerJson.PrayerApi
import com.programmergabut.solatkuy.ui.fragmentinfo.viewmodel.FragmentInfoViewModel
import com.programmergabut.solatkuy.util.EnumStatus
import com.programmergabut.solatkuy.util.Resource
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_compass.*
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.joda.time.LocalDate
import java.text.SimpleDateFormat
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 24/04/20.
 */

class FragmentInfo : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var fragmentInfoViewModel: FragmentInfoViewModel
    private var mMsApi1: MsApi1? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentInfoViewModel = ViewModelProviders.of(this).get(FragmentInfoViewModel::class.java)

        subscribeObserversAPI()

        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshLayout()
        subscribeObserversDB()
    }

    /* Subscribe live data */
    private fun subscribeObserversAPI() {
        val currDate = LocalDate()

        fragmentInfoViewModel.msApi1Local.observe(this, Observer {
            mMsApi1 = it
            fetchPrayerApi(it.latitude, it.longitude,"8", currDate.monthOfYear.toString(),currDate.year.toString())
        })
    }

    private fun subscribeObserversDB(){
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
                    tv_city.text = ""

                    tv_gregorian_date.text = gregorianDate?.date
                    tv_hijri_date.text = hijriDate?.date
                    tv_gregorian_day.text = gregorianDate?.weekday?.en
                    tv_hijri_day.text = hijriDate?.weekday?.en + " / " + hijriDate?.weekday?.ar
                }
                EnumStatus.LOADING -> {
                    tv_imsak_date.text = getString(R.string.loading)
                    tv_imsak_time.text = getString(R.string.loading)
                    tv_city.text = getString(R.string.loading)

                    tv_gregorian_date.text = getString(R.string.loading)
                    tv_hijri_date.text = getString(R.string.loading)
                    tv_gregorian_day.text = getString(R.string.loading)
                    tv_hijri_day.text = getString(R.string.loading)
                }
                EnumStatus.ERROR -> {
                    tv_imsak_date.text = getString(R.string.fetch_failed)
                    tv_imsak_time.text = getString(R.string.fetch_failed_sort)
                    tv_city.text = getString(R.string.fetch_failed)

                    tv_gregorian_date.text = getString(R.string.fetch_failed)
                    tv_hijri_date.text = getString(R.string.fetch_failed)
                    tv_gregorian_day.text = getString(R.string.fetch_failed)
                    tv_hijri_day.text = getString(R.string.fetch_failed)
                }
            }
        })
    }



    /* Create data from API */
    private fun createOnlineData(it: PrayerApi?, currentDate: String): Data? {
        return it?.data?.find { obj -> obj.date.gregorian.day == currentDate }
    }

    private fun fetchPrayerApi(latitude: String, longitude: String, method: String, month: String, year: String) {
        fragmentInfoViewModel.fetchPrayerApi(latitude,longitude,method, month,year)
    }

    /* Refresher */
    private fun refreshLayout() {
        sl_info.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        fragmentInfoViewModel.prayerApi.postValue(Resource.loading(null))
        val currDate = LocalDate()

        if(mMsApi1 != null)
            fetchPrayerApi(mMsApi1?.latitude!!, mMsApi1?.longitude!!, "8", currDate.monthOfYear.toString(),currDate.year.toString())
        else
            fragmentInfoViewModel.prayerApi.postValue(Resource.error(getString(R.string.fetch_failed), null))

        sl_info.isRefreshing = false
    }

}
