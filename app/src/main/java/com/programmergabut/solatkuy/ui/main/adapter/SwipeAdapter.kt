package com.programmergabut.solatkuy.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.programmergabut.solatkuy.ui.fragmentmain.view.FragmentMain
import com.programmergabut.solatkuy.ui.fragmentcompass.view.FragmentCompass
import com.programmergabut.solatkuy.ui.fragmentinfo.view.FragmentInfo
import com.programmergabut.solatkuy.ui.fragmentsetting.view.FragmentSetting

/*
 * Created by Katili Jiwo Adi Wiyono on 31/03/20.
 */

class SwipeAdapter(fm: FragmentManager): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> FragmentMain()
            1 -> FragmentCompass()
            2 -> FragmentInfo()
            3-> FragmentSetting()
            else -> error("Can't have more than 2 fragment on team detail")
        }
    }

    override fun getCount(): Int = 4

}