package com.programmergabut.solatkuy.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.programmergabut.solatkuy.ui.fragmentcompass.FragmentCompass
import com.programmergabut.solatkuy.ui.fragmentinfo.FragmentInfo
import com.programmergabut.solatkuy.ui.fragmentmain.FragmentMain
import com.programmergabut.solatkuy.ui.fragmentquran.QuranFragment
import com.programmergabut.solatkuy.ui.fragmentsetting.FragmentSetting

/*
 * Created by Katili Jiwo Adi Wiyono on 31/03/20.
 */

class SwipeAdapter(fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle) {

    private val arrayList: ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int = arrayList.size

    fun addFragment(fragment: Fragment?) {
        arrayList.add(fragment!!)
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> FragmentMain(false)
            1 -> FragmentCompass()
            2 -> QuranFragment()
            3 -> FragmentInfo()
            4 -> FragmentSetting()
            else -> error("SwipeAdapter")
        }
    }

}