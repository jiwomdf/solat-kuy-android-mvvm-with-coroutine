package com.programmergabut.solatkuy.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.programmergabut.solatkuy.data.PrayerRepositoryImpl
import com.programmergabut.solatkuy.data.QuranRepositoryImpl
import com.programmergabut.solatkuy.ui.main.fragmentfavayah.FavAyahFragment
import com.programmergabut.solatkuy.ui.main.fragmentfavayah.FavAyahViewModel
import com.programmergabut.solatkuy.ui.main.fragmentcompass.CompassFragment
import com.programmergabut.solatkuy.ui.main.fragmentcompass.FragmentCompassViewModel
import com.programmergabut.solatkuy.ui.main.fragmenthome.FragmentMainViewModel
import com.programmergabut.solatkuy.ui.main.fragmenthome.HomeFragment
import com.programmergabut.solatkuy.ui.main.fragmentquran.QuranFragment
import com.programmergabut.solatkuy.ui.main.fragmentquran.QuranFragmentViewModel
import com.programmergabut.solatkuy.ui.main.fragmentsetting.FragmentSettingViewModel
import com.programmergabut.solatkuy.ui.main.fragmentsetting.SettingFragment
import javax.inject.Inject

class SolatKuyFragmentFactory @Inject constructor(
    private val prayerRepositoryImpl: PrayerRepositoryImpl,
    private val quranRepositoryImpl: QuranRepositoryImpl
): FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            FavAyahFragment::class.java.name -> FavAyahFragment(
                FavAyahViewModel(quranRepositoryImpl)
            )
            CompassFragment::class.java.name -> CompassFragment(
                FragmentCompassViewModel(prayerRepositoryImpl)
            )
            QuranFragment::class.java.name -> QuranFragment(
                QuranFragmentViewModel(quranRepositoryImpl)
            )
            SettingFragment::class.java.name -> SettingFragment(
                FragmentSettingViewModel(prayerRepositoryImpl)
            )
            HomeFragment::class.java.name -> HomeFragment(
                FragmentMainViewModel(prayerRepositoryImpl,quranRepositoryImpl)
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}