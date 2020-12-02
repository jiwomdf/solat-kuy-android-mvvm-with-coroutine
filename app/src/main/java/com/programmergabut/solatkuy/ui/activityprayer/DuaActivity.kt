package com.programmergabut.solatkuy.ui.activityprayer

import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseActivity
import kotlinx.android.synthetic.main.activity_dua.*
import java.lang.Exception

class DuaActivity : BaseActivity(R.layout.activity_dua) {

    companion object{
        const val duaTitle = "duaTitle"
        const val duaAr = "duaAr"
        const val duaLt = "duaLt"
        const val duaEn = "duaEn"
        const val duaIn = "duaIn"
        const val duaRef = "duaRef"
    }

    override fun setFirstView() {
        try {
            tv_prayer_title.text = intent.getStringExtra(duaTitle)!!
            tv_prayer_ar.text = intent.getStringExtra(duaAr)!!
            tv_prayer_lt.text = intent.getStringExtra(duaLt)!!
            tv_prayer_en.text = intent.getStringExtra(duaEn)!!
            tv_prayer_in.text = intent.getStringExtra(duaIn)!!
            tv_prayer_ref.text = intent.getStringExtra(duaRef)!!
        }
        catch (ex: Exception){
            showBottomSheet(isCancelable = false, isFinish = true)
        }
    }



}