package com.programmergabut.solatkuy.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.databinding.LayoutErrorBottomsheetBinding
import com.programmergabut.solatkuy.util.SharedPrefUtil
import javax.inject.Inject

abstract class BaseActivity<DB: ViewDataBinding, VM: ViewModel>(
    private val layout: Int,
    private val viewModelClass: Class<VM>?,
): AppCompatActivity() {

    @Inject
    lateinit var db : SolatKuyRoom
    @Inject
    lateinit var sharedPrefUtil: SharedPrefUtil

    protected lateinit var binding : DB
    protected lateinit var viewModel: VM

    private lateinit var root : ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layout)
        binding.lifecycleOwner = this

        root = findViewById(android.R.id.content)

        viewModelClass?.let {
            viewModel = ViewModelProvider(this).get(it)
        }

        setListener()
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

    protected fun showBottomSheet(
        title : String = resources.getString(R.string.text_error_title), description: String = resources.getString(R.string.text_error_dsc),
        isCancelable: Boolean = true, isFinish: Boolean = false) {

        val dialogBinding = DataBindingUtil.inflate<LayoutErrorBottomsheetBinding>(
            layoutInflater, R.layout.layout_error_bottomsheet, null, true
        )
        val dialog = BottomSheetDialog(this)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(isCancelable)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.tvTitle.text = title
        dialogBinding.tvDesc.text = description
        dialog.show()

        dialogBinding.btnOk.setOnClickListener {

            if(isFinish){
                finish()
            }

            dialog.hide()
        }
    }

    protected fun getDatabase(): SolatKuyRoom {
        return db
    }

    protected fun insertLastReadSharedPref(selectedSurahId: Int, numberInSurah: Int) {
        sharedPrefUtil.insertLastReadSharedPref(selectedSurahId, numberInSurah)
    }

    protected fun getLastReadSurah(): Int {
        return sharedPrefUtil.getLastReadSurah()
    }

    protected fun getLastReadAyah(): Int {
        return sharedPrefUtil.getLastReadAyah()
    }

    protected fun getIsNotHasOpenAnimation(): Boolean {
        return sharedPrefUtil.getIsNotHasOpenAnimation()
    }

    protected fun setIsNotHasOpenAnimation(value: Boolean){
        sharedPrefUtil.setIsNotHasOpenAnimation(value)
    }

    protected fun getIsBrightnessActive(): Boolean {
        return sharedPrefUtil.getIsBrightnessActive()
    }

    protected fun setIsBrightnessActive(value: Boolean){
        sharedPrefUtil.setIsBrightnessActive(value)
    }

}