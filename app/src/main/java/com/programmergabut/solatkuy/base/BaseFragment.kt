package com.programmergabut.solatkuy.base

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import javax.inject.Inject

abstract class BaseFragment(fragmentLayout: Int) : Fragment(fragmentLayout) {

    @Inject
    lateinit var db: SolatKuyRoom
    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setIntentExtra()
        setFirstView()
        setObserver()
        setListener()
    }

    abstract fun setIntentExtra()
    abstract fun setFirstView()
    abstract fun setObserver()
    abstract fun setListener()

}