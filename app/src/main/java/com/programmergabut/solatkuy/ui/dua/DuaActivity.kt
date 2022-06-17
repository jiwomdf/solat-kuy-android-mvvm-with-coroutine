package com.programmergabut.solatkuy.ui.dua

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseActivity
import com.programmergabut.solatkuy.databinding.ActivityDuaBinding
import com.programmergabut.solatkuy.model.DuaExtraData
import java.lang.Exception
import java.lang.NullPointerException

class DuaActivity : BaseActivity<ActivityDuaBinding, ViewModel>(
    R.layout.activity_dua, null
) {

    companion object{
        const val DuaData = "dua_data"
        private const val TAG = "DuaActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setListener()
    }

    private fun setListener(){
        try {
            val duaData = intent.getParcelableExtra<DuaExtraData>(DuaData)
                ?: throw NullPointerException("DuaActivity getExtras")

            binding.apply {
                tvPrayerTitle.text = duaData.duaTitle
                tvPrayerAr.text = duaData.duaAr
                tvPrayerLt.text = duaData.duaLt
                tvPrayerEn.text = duaData.duaEn
                tvPrayerIn.text = duaData.duaIn
                tvPrayerRef.text = duaData.duaRef
            }
        }
        catch (ex: Exception){
            Log.d(TAG, ex.message.toString())
            showBottomSheet(isCancelable = false, isFinish = true)
        }
    }

    override fun getViewBinding() = ActivityDuaBinding.inflate(layoutInflater)

}