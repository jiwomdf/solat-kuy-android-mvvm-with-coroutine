package com.programmergabut.solatkuy.base

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import kotlinx.android.synthetic.main.layout_error_bottomsheet.*
import javax.inject.Inject

abstract class BaseActivity<VM: ViewModel>(private val contentView: Int, private val viewModelClass: Class<VM>?): AppCompatActivity() {

    @Inject
    lateinit var db : SolatKuyRoom
    @Inject
    lateinit var sharedPref: SharedPreferences

    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelClass?.let {
            viewModel = ViewModelProvider(this).get(viewModelClass)
        }


        setContentView(contentView)
        setIntentExtra()
        setFirstView()
        setObserver()
        setListener()
    }

    protected open fun setIntentExtra(){

    }
    protected open fun setFirstView(){

    }
    protected open fun setObserver(){

    }
    protected open fun setListener(){

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }

    protected fun <T : Any> gotoIntent(classIntent : Class<T>, bundle : Bundle?, isFinish : Boolean){
        val intent = Intent(this, classIntent)
        if(bundle != null)
            intent.putExtras(bundle)
        startActivity(intent)
        if(isFinish)
            finish()
    }

    protected fun showBottomSheet(title : String = resources.getString(R.string.text_error_title), description : String = resources.getString(R.string.text_error_dsc),
                                  isCancelable : Boolean = true, isFinish : Boolean = false) {

        val dialogView = layoutInflater.inflate(R.layout.layout_error_bottomsheet, null)
        val dialog =  BottomSheetDialog(this)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(isCancelable)
        dialog.setContentView(dialogView)
        dialog.tv_title.text = title
        dialog.tv_desc.text = description
        dialog.show()

        dialog.btn_ok.setOnClickListener {

            if(isFinish){
                finish()
            }

            dialog.hide()
        }
    }

    protected fun getDatabase() = db

    protected fun insertLastReadSharedPref(mSelSurahId: Int, numberInSurah: Int) {
        sharedPref.edit()?.apply{
            putInt(EnumConfig.LAST_READ_SURAH, mSelSurahId)
            putInt(EnumConfig.LAST_READ_AYAH, numberInSurah)
            apply()
        }
    }

    protected fun getLastReadSurah(): Int {
        return sharedPref.getInt(EnumConfig.LAST_READ_SURAH, -1)
    }

    protected fun getLastReadAyah(): Int {
        return sharedPref.getInt(EnumConfig.LAST_READ_AYAH, -1)
    }

    protected fun getIsNotHasOpenAnimation(): Boolean {
        return sharedPref.getBoolean("isHasNotOpenAnimation", true)
    }

    protected fun setIsNotHasOpenAnimation(value: Boolean){
        sharedPref.edit()?.apply{
            putBoolean("isHasNotOpenAnimation", value)
            apply()
        }
    }

    protected fun getIsBrightnessActive(): Boolean {
        return sharedPref.getBoolean("isBrightnessActive", false)
    }

    protected fun setIsBrightnessActive(value: Boolean){
        sharedPref.edit()?.apply{
            putBoolean("isBrightnessActive", value)
            apply()
        }
    }

}