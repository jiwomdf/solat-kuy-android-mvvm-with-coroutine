package com.programmergabut.solatkuy.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programmergabut.solatkuy.data.repository.Repository
import com.programmergabut.solatkuy.di.Injection
import com.programmergabut.solatkuy.ui.fragmentcompass.viewmodel.FragmentCompassViewModel
import com.programmergabut.solatkuy.ui.fragmentinfo.viewmodel.FragmentInfoViewModel
import com.programmergabut.solatkuy.ui.fragmentmain.viewmodel.FragmentMainViewModel
import com.programmergabut.solatkuy.ui.fragmentsetting.viewmodel.FragmentSettingViewModel
import com.programmergabut.solatkuy.ui.main.viewmodel.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope

class ViewModelFactory(private val application: Application, private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {

    companion object{
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(application: Application, scope: CoroutineScope): ViewModelFactory{
            return instance ?: synchronized(this){
                instance ?: ViewModelFactory(application, Injection.provideRepository(application, scope))
            }
        }
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return when{
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> MainActivityViewModel(application, repository) as T
            modelClass.isAssignableFrom(FragmentSettingViewModel::class.java) -> FragmentSettingViewModel(application, repository) as T
            modelClass.isAssignableFrom(FragmentMainViewModel::class.java) -> FragmentMainViewModel(application, repository) as T
            modelClass.isAssignableFrom(FragmentCompassViewModel::class.java) -> FragmentCompassViewModel(application, repository) as T
            modelClass.isAssignableFrom(FragmentInfoViewModel::class.java) -> FragmentInfoViewModel(application, repository) as T
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    }
}