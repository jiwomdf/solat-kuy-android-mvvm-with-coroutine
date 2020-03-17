package com.programmergabut.solatkuy.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programmergabut.solatkuy.data.api.ApiHelper
import com.programmergabut.solatkuy.data.repository.MainRepository
import com.programmergabut.solatkuy.ui.main.viewmodel.MainViewModel

class ViewModelFactory(private val apiHelper: ApiHelper): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(MainRepository(apiHelper)) as T
        else
            throw IllegalArgumentException("Class not defined")

    }
}