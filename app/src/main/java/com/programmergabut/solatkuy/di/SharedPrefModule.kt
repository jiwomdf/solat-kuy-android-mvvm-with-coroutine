package com.programmergabut.solatkuy.di

import android.content.Context
import android.content.SharedPreferences
import com.programmergabut.solatkuy.util.Constant
import com.programmergabut.solatkuy.util.SharedPrefUtil
import org.koin.dsl.module


val sharedPrefModule = module {


    fun provideSharedPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    single { provideSharedPref(get()) }
    single { SharedPrefUtil(get()) }

}
