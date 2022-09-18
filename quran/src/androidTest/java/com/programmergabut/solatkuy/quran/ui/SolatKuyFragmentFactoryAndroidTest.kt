package com.programmergabut.solatkuy.quran.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.programmergabut.solatkuy.quran.data.FakePrayerRepositoryAndroidTest
import com.programmergabut.solatkuy.quran.data.FakeQuranRepositoryAndroidTest
import com.programmergabut.solatkuy.quran.ui.listsurah.ListSurahFragment
import com.programmergabut.solatkuy.quran.ui.listsurah.ListSurahViewModel
import com.programmergabut.solatkuy.quran.ui.readsurah.ReadSurahFragment
import com.programmergabut.solatkuy.quran.ui.readsurah.ReadSurahViewModel
import com.programmergabut.solatkuy.ui.main.qibla.CompassFragment
import com.programmergabut.solatkuy.ui.main.qibla.CompassViewModel
import com.programmergabut.solatkuy.ui.main.home.HomeFragment
import com.programmergabut.solatkuy.ui.main.home.HomeViewModel
import com.programmergabut.solatkuy.ui.main.setting.SettingViewModel
import com.programmergabut.solatkuy.ui.main.setting.SettingFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class SolatKuyFragmentFactoryAndroidTest @Inject constructor() : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            ReadSurahFragment::class.java.name -> ReadSurahFragment(
                ReadSurahViewModel(
                    FakeQuranRepositoryAndroidTest()
                )
            )
            CompassFragment::class.java.name -> CompassFragment(
                CompassViewModel(FakePrayerRepositoryAndroidTest())
            )
            ListSurahFragment::class.java.name -> ListSurahFragment(
                ListSurahViewModel(
                    FakeQuranRepositoryAndroidTest()
                )
            )
            SettingFragment::class.java.name -> SettingFragment(
                SettingViewModel(FakePrayerRepositoryAndroidTest())
            )
            HomeFragment::class.java.name -> HomeFragment(
                HomeViewModel(FakePrayerRepositoryAndroidTest(), FakeQuranRepositoryAndroidTest())
            )
            else -> super.instantiate(classLoader, className)
        }

    }
}