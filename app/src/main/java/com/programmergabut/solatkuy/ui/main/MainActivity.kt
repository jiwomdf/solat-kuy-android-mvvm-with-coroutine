package com.programmergabut.solatkuy.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseActivity
import com.programmergabut.solatkuy.databinding.ActivityMainBinding
import com.programmergabut.solatkuy.ui.SolatKuyFragmentFactory
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import javax.inject.Inject


class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(
    R.layout.activity_main,
    MainViewModel::class
) {

    private val TAG = "MainActivity"

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBottomNav()
        setupKoinFragmentFactory()
    }

    override fun onDestroy() {
        sharedPrefUtil.setIsHasOpenAnimation(false)
        super.onDestroy()
    }

    private fun initBottomNav() {
        try {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
            val navController = navHostFragment.navController
            binding.bottomNavigation.setupWithNavController(navController)
            navHostFragment.findNavController()
                .addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.fragmentHome,
                        R.id.fragmentCompass,
                        R.id.fragmentQuran,
                        R.id.fragmentSetting
                        -> binding.bottomNavigation.visibility = View.VISIBLE
                        else
                        -> binding.bottomNavigation.visibility = View.GONE
                    }
                }
            binding.bottomNavigation.setOnNavigationItemReselectedListener {/* NO-OP */ }
        } catch (ex: Exception) {
            Log.d(TAG, ex.message.toString())
        }
    }

}
