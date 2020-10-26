package com.programmergabut.solatkuy.base

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import javax.inject.Inject

abstract class BaseActivity(
    private val contentView: Int
): AppCompatActivity() {

    @Inject
    lateinit var db : SolatKuyRoom
    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(contentView)
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