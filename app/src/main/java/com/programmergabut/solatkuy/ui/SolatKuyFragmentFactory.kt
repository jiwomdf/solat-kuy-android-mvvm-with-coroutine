package com.programmergabut.solatkuy.ui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.programmergabut.solatkuy.data.repository.PrayerRepositoryImpl
import com.programmergabut.solatkuy.data.repository.QuranRepositoryImpl
import com.programmergabut.solatkuy.ui.main.home.HomeFragment
import com.programmergabut.solatkuy.ui.main.home.HomeViewModel
import com.programmergabut.solatkuy.ui.main.qibla.CompassFragment
import com.programmergabut.solatkuy.ui.main.qibla.CompassViewModel
import com.programmergabut.solatkuy.ui.main.setting.SettingFragment
import com.programmergabut.solatkuy.ui.main.setting.SettingViewModel
import com.programmergabut.solatkuy.util.packageutil.Quran
import java.lang.reflect.Constructor
import javax.inject.Inject


class SolatKuyFragmentFactory @Inject constructor(
    private val prayerRepositoryImpl: PrayerRepositoryImpl,
    private val quranRepositoryImpl: QuranRepositoryImpl
): FragmentFactory() {

    val listSurahFragment: Class<*> = Class.forName(Quran.ListSurahFragment.reflection)
    val listSurahViewModel: Class<*> = Class.forName(Quran.ListSurahViewModel.reflection)

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            CompassFragment::class.java.name -> CompassFragment(
                CompassViewModel(prayerRepositoryImpl)
            )
            listSurahFragment::class.java.name -> {
                val listSurahFragmentConstructor: Constructor<*> = listSurahFragment.getConstructor(Context::class.java)
                val listListSurahViewModel: Constructor<*> = listSurahViewModel.getConstructor(Context::class.java)
                listSurahFragmentConstructor.newInstance(
                    listListSurahViewModel.newInstance(quranRepositoryImpl)
                ) as Fragment
            }
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