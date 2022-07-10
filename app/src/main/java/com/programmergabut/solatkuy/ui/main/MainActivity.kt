package com.programmergabut.solatkuy.ui.main

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseActivity
import com.programmergabut.solatkuy.databinding.ActivityMainBinding
import com.programmergabut.solatkuy.ui.SolatKuyFragmentFactory
import com.programmergabut.solatkuy.util.packageutil.Quran
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*
 * Created by Katili Jiwo Adi Wiyono on 25/03/20.
 */
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, ViewModel>(
    R.layout.activity_main,
    null
) {

    private val TAG = "MainActivity"

    @Inject
    lateinit var fragmentFactory: SolatKuyFragmentFactory

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBottomNav()
        supportFragmentManager.fragmentFactory = fragmentFactory
    }

    override fun onDestroy() {
        sharedPrefUtil.setIsHasOpenAnimation(false)
        super.onDestroy()
    }

    private fun initBottomNav() {
        try{
            binding.apply {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
                val navController = navHostFragment.navController
                bottomNavigation.setupWithNavController(navController)
                navHostFragment.findNavController()
                    .addOnDestinationChangedListener { _, destination, _ ->
                        when(destination.id){
                            R.id.fragmentQuran,
                            R.id.fragmentHome,
                            R.id.fragmentCompass,
                            R.id.fragmentSetting
                            -> bottomNavigation.visibility = View.VISIBLE
                            else
                            -> bottomNavigation.visibility = View.GONE
                        }
                    }
                bottomNavigation.setOnNavigationItemReselectedListener {/* NO-OP */ }
            }
        }
        catch (ex: Exception){
            Log.d(TAG, ex.message.toString())
        }
    }

}
