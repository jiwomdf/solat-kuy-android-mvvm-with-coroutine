package com.programmergabut.solatkuy.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.programmergabut.solatkuy.data.FakePrayerRepositoryAndroidTest
import com.programmergabut.solatkuy.data.FakeQuranRepositoryAndroidTest
import com.programmergabut.solatkuy.ui.main.qibla.CompassFragment
import com.programmergabut.solatkuy.ui.main.qibla.CompassViewModel
import com.programmergabut.solatkuy.ui.main.home.FragmentMainViewModel
import com.programmergabut.solatkuy.ui.main.home.HomeFragment
import com.programmergabut.solatkuy.ui.main.quran.listsurah.ListSurahFragment
import com.programmergabut.solatkuy.ui.main.quran.listsurah.ListSurahViewModel
import com.programmergabut.solatkuy.ui.main.setting.FragmentSettingViewModel
import com.programmergabut.solatkuy.ui.main.setting.SettingFragment
import com.programmergabut.solatkuy.ui.main.quran.favayah.FavAyahFragment
import com.programmergabut.solatkuy.ui.main.quran.favayah.FavAyahViewModel
import com.programmergabut.solatkuy.ui.main.quran.readsurah.ReadSurahFragment
import com.programmergabut.solatkuy.ui.main.quran.readsurah.ReadSurahViewModel
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
                CompassViewModel(FakePrayerRepositoryAndroidTest())
            )
            ListSurahFragment::class.java.name -> ListSurahFragment(
                ListSurahViewModel(FakeQuranRepositoryAndroidTest())
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