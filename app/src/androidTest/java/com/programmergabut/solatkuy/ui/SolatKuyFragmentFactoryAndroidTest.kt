package com.programmergabut.solatkuy.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.programmergabut.solatkuy.ui.main.fragmentcompass.CompassFragment
import com.programmergabut.solatkuy.ui.main.fragmentcompass.FragmentCompassViewModel
import com.programmergabut.solatkuy.ui.main.fragmenthome.FragmentMainViewModel
import com.programmergabut.solatkuy.ui.main.fragmenthome.HomeFragment
import com.programmergabut.solatkuy.ui.main.fragmentquran.QuranFragment
import com.programmergabut.solatkuy.ui.main.fragmentquran.QuranFragmentViewModel
import com.programmergabut.solatkuy.ui.main.fragmentsetting.FragmentSettingViewModel
import com.programmergabut.solatkuy.ui.main.fragmentsetting.SettingFragment
import com.programmergabut.solatkuy.data.FakePrayerRepositoryAndroidTest
import com.programmergabut.solatkuy.data.FakeQuranRepositoryAndroidTest
import com.programmergabut.solatkuy.ui.main.fragmentfavayah.FavAyahFragment
import com.programmergabut.solatkuy.ui.main.fragmentfavayah.FavAyahViewModel
import com.programmergabut.solatkuy.ui.main.fragmentreadsurah.ReadSurahFragment
import com.programmergabut.solatkuy.ui.main.fragmentreadsurah.ReadSurahViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class SolatKuyFragmentFactoryAndroidTest @Inject constructor() : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            ReadSurahFragment::class.java.name -> ReadSurahFragment(
                ReadSurahViewModel(FakeQuranRepositoryAndroidTest())
            )
            FavAyahFragment::class.java.name -> FavAyahFragment(
                FavAyahViewModel(FakeQuranRepositoryAndroidTest())
            )
            CompassFragment::class.java.name -> CompassFragment(
                FragmentCompassViewModel(FakePrayerRepositoryAndroidTest())
            )
            QuranFragment::class.java.name -> QuranFragment(
                QuranFragmentViewModel(FakeQuranRepositoryAndroidTest())
            )
            SettingFragment::class.java.name -> SettingFragment(
                FragmentSettingViewModel(FakePrayerRepositoryAndroidTest())
            )
            HomeFragment::class.java.name -> HomeFragment(
                FragmentMainViewModel(FakePrayerRepositoryAndroidTest(), FakeQuranRepositoryAndroidTest())
            )
            else -> super.instantiate(classLoader, className)
        }

    }
}