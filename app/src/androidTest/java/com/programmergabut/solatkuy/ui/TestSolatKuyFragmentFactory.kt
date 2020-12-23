package com.programmergabut.solatkuy.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.programmergabut.solatkuy.ui.activitymain.fragmentcompass.CompassFragment
import com.programmergabut.solatkuy.ui.activitymain.fragmentcompass.FragmentCompassViewModel
import com.programmergabut.solatkuy.ui.activitymain.fragmentmain.FragmentMainViewModel
import com.programmergabut.solatkuy.ui.activitymain.fragmentmain.MainFragment
import com.programmergabut.solatkuy.ui.activitymain.fragmentquran.QuranFragment
import com.programmergabut.solatkuy.ui.activitymain.fragmentquran.QuranFragmentViewModel
import com.programmergabut.solatkuy.ui.activitymain.fragmentsetting.FragmentSettingViewModel
import com.programmergabut.solatkuy.ui.activitymain.fragmentsetting.SettingFragment
import com.programmergabut.solatkuy.viewmodel.FakePrayerRepositoryAndroidTest
import com.programmergabut.solatkuy.viewmodel.FakeQuranRepositoryAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class TestSolatKuyFragmentFactory @Inject constructor() : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
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