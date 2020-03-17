package com.programmergabut.solatkuy.ui.main.view

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.badge.BadgeDrawable
import com.programmergabut.solatkuy.R
import kotlinx.android.synthetic.main.layout_bottom_bar.*


class MainActivity : AppCompatActivity() {

    private var menuList: Menu? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val badge: BadgeDrawable = bottom_navigation.getOrCreateBadge(1)
        badge.isVisible = true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        this.menuList = menu

        menuInflater.inflate(R.menu.bottom_navigation_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
