package com.programmergabut.solatkuy.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programmergabut.solatkuy.data.api.ApiHelper
import com.programmergabut.solatkuy.data.repository.MainRepository
import com.programmergabut.solatkuy.ui.main.view.MainActivity
import com.programmergabut.solatkuy.ui.main.viewmodel.MainActivityViewModel
import com.programmergabut.solatkuy.ui.prayerdetail.viewmodel.ActivityPrayerViewModel

class ViewModelFactory(private val apiHelper: ApiHelper): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ActivityPrayerViewModel::class.java))
            return ActivityPrayerViewModel(MainRepository(apiHelper)) as T
        else if(modelClass.isAssignableFrom(MainActivityViewModel::class.java))
            return MainActivityViewModel(MainRepository(apiHelper)) as T
        else
            throw IllegalArgumentException("Class not defined")

    }
}