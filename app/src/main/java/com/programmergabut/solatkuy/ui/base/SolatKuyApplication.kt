package com.programmergabut.solatkuy.ui.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class SolatKuyApplication: Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}