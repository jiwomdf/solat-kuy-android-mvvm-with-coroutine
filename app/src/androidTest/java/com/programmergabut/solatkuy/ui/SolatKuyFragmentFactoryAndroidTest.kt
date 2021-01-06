package com.programmergabut.solatkuy.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.programmergabut.solatkuy.ui.fragmentcompass.CompassFragment
import com.programmergabut.solatkuy.ui.fragmentcompass.FragmentCompassViewModel
import com.programmergabut.solatkuy.ui.fragmentmain.FragmentMainViewModel
import com.programmergabut.solatkuy.ui.fragmentmain.MainFragment
import com.programmergabut.solatkuy.ui.fragmentquran.QuranFragment
import com.programmergabut.solatkuy.ui.fragmentquran.QuranFragmentViewModel
import com.programmergabut.solatkuy.ui.fragmentsetting.FragmentSettingViewModel
import com.programmergabut.solatkuy.ui.fragmentsetting.SettingFragment
import com.programmergabut.solatkuy.data.FakePrayerRepositoryAndroidTest
import com.programmergabut.solatkuy.data.FakeQuranRepositoryAndroidTest
import com.programmergabut.solatkuy.ui.fragmentfavayah.FavAyahFragment
import com.programmergabut.solatkuy.ui.fragmentfavayah.FavAyahViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class SolatKuyFragmentFactoryAndroidTest @Inject constructor() : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
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
            MainFragment::class.java.name -> MainFragment(
                FragmentMainViewModel(FakePrayerRepositoryAndroidTest(), FakeQuranRepositoryAndroidTest())
            )
            else -> super.instantiate(classLoader, className)
        }

    }
}