package com.programmergabut.solatkuy.ui.splash

import android.os.Handler
import android.os.Looper
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseActivity
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.databinding.ActivitySplashBinding
import com.programmergabut.solatkuy.ui.boarding.BoardingActivity
import com.programmergabut.solatkuy.ui.main.MainActivity


class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(
    R.layout.activity_splash,
    SplashViewModel::class
) {

    private val splashDelay: Long = 1000

    override fun getViewBinding() = ActivitySplashBinding.inflate(layoutInflater)

    override fun setListener() {
        super.setListener()
        observeDb()
    }

    private fun observeDb() {
        viewModel.msSetting.observe(this, {
            if (it != null) {
                if (it.isHasOpenApp) {
                    gotoMainActivity()
                } else
                    gotoBoardingActivity()

            } else
                SolatKuyRoom.populateDatabase(viewModel.solatKuyRoom)

        })
    }

    private fun gotoMainActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            gotoIntent(MainActivity::class.java, null, true)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, splashDelay)
    }

    private fun gotoBoardingActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            gotoIntent(BoardingActivity::class.java, null, true)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, splashDelay)
    }
}