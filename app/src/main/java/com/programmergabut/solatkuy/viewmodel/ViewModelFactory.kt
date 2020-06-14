package com.programmergabut.solatkuy.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programmergabut.solatkuy.data.Repository
import com.programmergabut.solatkuy.di.Injection
import com.programmergabut.solatkuy.ui.activityfavayah.FavAyahViewModel
import com.programmergabut.solatkuy.ui.activityreadsurah.ReadSurahViewModel
import com.programmergabut.solatkuy.ui.fragmentcompass.viewmodel.FragmentCompassViewModel
import com.programmergabut.solatkuy.ui.fragmentinfo.viewmodel.FragmentInfoViewModel
import com.programmergabut.solatkuy.ui.fragmentmain.viewmodel.FragmentMainViewModel
import com.programmergabut.solatkuy.ui.fragmentquran.QuranFragmentViewModel
import com.programmergabut.solatkuy.ui.fragmentsetting.viewmodel.FragmentSettingViewModel
import com.programmergabut.solatkuy.ui.main.viewmodel.MainActivityViewModel

class ViewModelFactory(private val application: Application, private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {

    companion object{
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(application: Application): ViewModelFactory{
            return instance ?: synchronized(this){
                instance ?: ViewModelFactory(application, Injection.provideRepository(application))
            }
        }
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return when{
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> MainActivityViewModel(application, repository) as T
            modelClass.isAssignableFrom(FragmentSettingViewModel::class.java) -> FragmentSettingViewModel(application, repository) as T
            modelClass.isAssignableFrom(FragmentMainViewModel::class.java) -> FragmentMainViewModel(application, repository) as T
            modelClass.isAssignableFrom(FragmentCompassViewModel::class.java) -> FragmentCompassViewModel(application, repository) as T
            modelClass.isAssignableFrom(QuranFragmentViewModel::class.java) -> QuranFragmentViewModel(application, repository) as T
            modelClass.isAssignableFrom(FragmentInfoViewModel::class.java) -> FragmentInfoViewModel(application, repository) as T
            modelClass.isAssignableFrom(ReadSurahViewModel::class.java) -> ReadSurahViewModel(application, repository) as T
            modelClass.isAssignableFrom(FavAyahViewModel::class.java) -> FavAyahViewModel(application, repository) as T
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    }
}