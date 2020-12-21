package com.programmergabut.solatkuy.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.programmergabut.solatkuy.data.PrayerRepositoryImpl
import com.programmergabut.solatkuy.data.QuranRepositoryImpl
import com.programmergabut.solatkuy.ui.activityfavayah.FavAyahActivity
import com.programmergabut.solatkuy.ui.activityfavayah.FavAyahViewModel
import com.programmergabut.solatkuy.ui.fragmentcompass.CompassFragment
import com.programmergabut.solatkuy.ui.fragmentcompass.FragmentCompassViewModel
import com.programmergabut.solatkuy.ui.fragmentinfo.FragmentInfoViewModel
import com.programmergabut.solatkuy.ui.fragmentinfo.InfoFragment
import com.programmergabut.solatkuy.ui.fragmentmain.FragmentMainViewModel
import com.programmergabut.solatkuy.ui.fragmentmain.MainFragment
import com.programmergabut.solatkuy.ui.fragmentquran.QuranFragment
import com.programmergabut.solatkuy.ui.fragmentquran.QuranFragmentViewModel
import com.programmergabut.solatkuy.ui.fragmentsetting.FragmentSettingViewModel
import com.programmergabut.solatkuy.ui.fragmentsetting.SettingFragment
import javax.inject.Inject

class SolatKuyFragmentFactory @Inject constructor(
    private val prayerRepositoryImpl: PrayerRepositoryImpl,
    private val quranRepositoryImpl: QuranRepositoryImpl
): FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            CompassFragment::class.java.name -> CompassFragment(
                FragmentCompassViewModel(prayerRepositoryImpl)
            )
            InfoFragment::class.java.name -> InfoFragment(
                FragmentInfoViewModel(prayerRepositoryImpl)
            )
            QuranFragment::class.java.name -> QuranFragment(
                QuranFragmentViewModel(quranRepositoryImpl)
            )
            SettingFragment::class.java.name -> SettingFragment(
                FragmentSettingViewModel(prayerRepositoryImpl)
            )
            MainFragment::class.java.name -> MainFragment(
                FragmentMainViewModel(prayerRepositoryImpl,quranRepositoryImpl)
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}