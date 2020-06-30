package com.programmergabut.solatkuy.viewmodel

import android.app.Application
import android.content.Context
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
import com.programmergabut.solatkuy.util.helper.NetworkHelper

/* @Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: Repository,
                       private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    companion object{
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(application: Application, context: Context): ViewModelFactory{
            return instance ?: synchronized(this){
                instance ?: ViewModelFactory(Injection.provideRepository(application), context)
            }
        }
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val networkHelper = NetworkHelper.getInstance(context)

        return when{
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> MainActivityViewModel(repository) as T
            modelClass.isAssignableFrom(FragmentSettingViewModel::class.java) -> FragmentSettingViewModel(repository) as T
            modelClass.isAssignableFrom(FragmentMainViewModel::class.java) -> FragmentMainViewModel(repository, networkHelper) as T
            modelClass.isAssignableFrom(FragmentCompassViewModel::class.java) -> FragmentCompassViewModel(repository, networkHelper) as T
            modelClass.isAssignableFrom(QuranFragmentViewModel::class.java) -> QuranFragmentViewModel(repository, networkHelper) as T
            modelClass.isAssignableFrom(FragmentInfoViewModel::class.java) -> FragmentInfoViewModel(repository, networkHelper) as T
            modelClass.isAssignableFrom(ReadSurahViewModel::class.java) -> ReadSurahViewModel(repository, networkHelper) as T
            modelClass.isAssignableFrom(FavAyahViewModel::class.java) -> FavAyahViewModel(repository) as T
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    }
} */