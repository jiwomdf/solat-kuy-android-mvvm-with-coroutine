package com.programmergabut.solatkuy.ui.activityprayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.programmergabut.solatkuy.R
import kotlinx.android.synthetic.main.activity_dua.*

class DuaActivity : AppCompatActivity() {

    companion object{
        const val duaTitle = "duaTitle"
        const val duaAr = "duaAr"
        const val duaLt = "duaLt"
        const val duaEn = "duaEn"
        const val duaIn = "duaIn"
        const val duaRef = "duaRef"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dua)

        tv_prayer_title.text = intent.getStringExtra(duaTitle)!!
        tv_prayer_ar.text = intent.getStringExtra(duaAr)!!
        tv_prayer_lt.text = intent.getStringExtra(duaLt)!!
        tv_prayer_en.text = intent.getStringExtra(duaEn)!!
        tv_prayer_in.text = intent.getStringExtra(duaIn)!!
        tv_prayer_ref.text = intent.getStringExtra(duaRef)!!
    }

}