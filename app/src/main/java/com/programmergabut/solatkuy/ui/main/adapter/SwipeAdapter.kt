package com.programmergabut.solatkuy.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.programmergabut.solatkuy.ui.fragmentmain.view.FragmentMain
import com.programmergabut.solatkuy.ui.fragmentprayerreport.view.FragmentPrayerReport
import com.programmergabut.solatkuy.ui.fragmentsetting.view.FragmentSetting

class SwipeAdapter(fm: FragmentManager): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> FragmentMain()
            1 -> FragmentPrayerReport()
            else -> FragmentSetting()
        }
    }

    override fun getCount(): Int = 3

}