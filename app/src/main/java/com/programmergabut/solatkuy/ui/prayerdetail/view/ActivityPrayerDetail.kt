package com.programmergabut.solatkuy.ui.prayerdetail.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.model.prayerJson.PrayerApi
import com.programmergabut.solatkuy.ui.prayerdetail.adapter.ActivityPrayerAdapter
import com.programmergabut.solatkuy.ui.prayerdetail.viewmodel.ActivityPrayerViewModel
import kotlinx.android.synthetic.main.activity_prayer_detail.*

class ActivityPrayerDetail : AppCompatActivity() {

    private lateinit var adapter: ActivityPrayerAdapter
    private lateinit var activityPrayerViewModel: ActivityPrayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prayer_detail)

        setupUI()
        setupViewModel()
        setupAPICall()
    }

    private fun setupUI(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter =  ActivityPrayerAdapter(arrayListOf())
        recyclerView.adapter = adapter
    }

    private fun setupViewModel(){
//        activityPrayerViewModel = ViewModelProviders.of(this, ViewModelFactory(ApiHelper(ApiServiceImpl()),application))
//            .get(ActivityPrayerViewModel::class.java)
    }

    private fun setupAPICall(){
//        activityPrayerViewModel.getPrayer().observe(this, Observer {
//
//            when(it.Status){
//                EnumStatus.SUCCESS -> {
//                    progressBar.visibility = View.GONE
//                    it.data?.let { prayer -> renderList(prayer)}
//                    recyclerView.visibility = View.VISIBLE
//                }
//                EnumStatus.LOADING -> {
//                    progressBar.visibility = View.VISIBLE
//                    recyclerView.visibility = View.GONE
//                }
//                EnumStatus.ERROR -> {
//                    progressBar.visibility = View.GONE
//                    Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
//                }
//            }
//
//        })
//
//        activityPrayerViewModel.fetchPrayer()
    }

    private fun renderList(prayer: PrayerApi) {
        adapter.addData(prayer)
        adapter.notifyDataSetChanged()
    }

}
