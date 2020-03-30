package com.programmergabut.solatkuy.ui.main.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.ui.main.adapter.SwipeAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_bottom_navigation_bar.*
import kotlin.math.abs


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener(this)

        vp2_main.adapter = SwipeAdapter(supportFragmentManager)
        vp2_main.setPageTransformer(true, ZoomOutPageTransformer())
        vp2_main.addOnPageChangeListener( object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> bottom_navigation.menu.findItem(R.id.i_prayer_time).isChecked  = true
                    1 -> bottom_navigation.menu.findItem(R.id.i_prayer_time2).isChecked = true
                    2 -> bottom_navigation.menu.findItem(R.id.i_prayer_time3).isChecked = true
                }
            }

        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.i_prayer_time -> vp2_main.currentItem = 0
            R.id.i_prayer_time2 -> vp2_main.currentItem = 1
            R.id.i_prayer_time3 -> vp2_main.currentItem = 2
        }
        return true
    }


    private inner class ZoomOutPageTransformer : ViewPager.PageTransformer {

        private val MIN_SCALE = 0.85f
        private val MIN_ALPHA = 0.5f

        override fun transformPage(view: View, position: Float) {
            view.apply {

                val pageWidth = width
                val pageHeight = height

                when {
                    position < -1 -> alpha = 0f
                    position <= 1 -> {
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) horzMargin - vertMargin / 2 else horzMargin + vertMargin / 2

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        alpha = (MIN_ALPHA + (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> alpha = 0f
                }
            }
        }

    }


}
