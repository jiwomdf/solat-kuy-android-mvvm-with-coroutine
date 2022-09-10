package com.programmergabut.solatkuy.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.programmergabut.solatkuy.data.repository.PrayerRepositoryImpl
import com.programmergabut.solatkuy.data.repository.QuranRepositoryImpl
import com.programmergabut.solatkuy.ui.main.qibla.CompassFragment
import com.programmergabut.solatkuy.ui.main.qibla.CompassViewModel
import com.programmergabut.solatkuy.ui.main.home.HomeFragment
import com.programmergabut.solatkuy.ui.main.home.HomeViewModel
import com.programmergabut.solatkuy.ui.main.quran.listsurah.ListSurahFragment
import com.programmergabut.solatkuy.ui.main.quran.listsurah.ListSurahViewModel
import com.programmergabut.solatkuy.ui.main.setting.SettingViewModel
import com.programmergabut.solatkuy.ui.main.setting.SettingFragment
import javax.inject.Inject

class SolatKuyFragmentFactory @Inject constructor(
    private val prayerRepositoryImpl: PrayerRepositoryImpl,
    private val quranRepositoryImpl: QuranRepositoryImpl
): FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            CompassFragment::class.java.name -> CompassFragment(
                CompassViewModel(prayerRepositoryImpl)
            )
            ListSurahFragment::class.java.name -> ListSurahFragment(
                ListSurahViewModel(quranRepositoryImpl)
            )
            SettingFragment::class.java.name -> SettingFragment(
                SettingViewModel(prayerRepositoryImpl)
            )
            HomeFragment::class.java.name -> HomeFragment(
                HomeViewModel(prayerRepositoryImpl,quranRepositoryImpl)
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}