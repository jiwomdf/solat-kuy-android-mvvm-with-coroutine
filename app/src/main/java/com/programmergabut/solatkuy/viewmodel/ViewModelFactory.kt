package com.programmergabut.solatkuy.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.ui.activityfavayah.FavAyahViewModel
import com.programmergabut.solatkuy.ui.activityreadsurah.ReadSurahViewModel
import com.programmergabut.solatkuy.ui.fragmentcompass.FragmentCompassViewModel
import com.programmergabut.solatkuy.ui.fragmentinfo.FragmentInfoViewModel
import com.programmergabut.solatkuy.ui.fragmentmain.FragmentMainViewModel
import com.programmergabut.solatkuy.ui.fragmentquran.QuranFragmentViewModel
import com.programmergabut.solatkuy.ui.fragmentsetting.FragmentSettingViewModel
import com.programmergabut.solatkuy.ui.main.MainActivityViewModel

/* @Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {

    companion object{
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(application: Application): ViewModelFactory{
            return instance ?: synchronized(this){
                instance ?: ViewModelFactory(Injection.provideRepository(application))
            }
        }
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return when{
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> MainActivityViewModel(repository) as T
            modelClass.isAssignableFrom(FragmentSettingViewModel::class.java) -> FragmentSettingViewModel(repository) as T
            modelClass.isAssignableFrom(FragmentMainViewModel::class.java) -> FragmentMainViewModel(repository) as T
            modelClass.isAssignableFrom(FragmentCompassViewModel::class.java) -> FragmentCompassViewModel(repository) as T
            modelClass.isAssignableFrom(QuranFragmentViewModel::class.java) -> QuranFragmentViewModel(repository) as T
            modelClass.isAssignableFrom(FragmentInfoViewModel::class.java) -> FragmentInfoViewModel(repository) as T
            modelClass.isAssignableFrom(ReadSurahViewModel::class.java) -> ReadSurahViewModel(repository) as T
            modelClass.isAssignableFrom(FavAyahViewModel::class.java) -> FavAyahViewModel(repository) as T
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    }
} */