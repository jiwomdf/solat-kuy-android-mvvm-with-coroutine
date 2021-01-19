package com.programmergabut.solatkuy.base

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
    protected val LOCATION_PERMISSIONS = 101
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

    protected fun isLocationPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    protected fun listLocationPermission(): Array<String> {
        return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
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
        title : String = resources.getString(R.string.text_error_title),
        description: String = resources.getString(R.string.text_error_dsc),
        isCancelable: Boolean = true,
        isFinish: Boolean = false,
        callback: (() -> Unit)? = null) {

        val dialog = BottomSheetDialog(this)
        val dialogBinding = DataBindingUtil.inflate<LayoutErrorBottomsheetBinding>(
            layoutInflater, R.layout.layout_error_bottomsheet, null, true
        )
        dialog.apply{
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setCancelable(isCancelable)
            setContentView(dialogBinding.root)
        }
        dialogBinding.apply{
            tvTitle.text = title
            tvDesc.text = description
        }
        dialog.show()
        dialogBinding.btnOk.setOnClickListener {
            dialog.hide()
            callback?.invoke()
            if(isFinish)
                finish()
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
        return sharedPrefUtil.getIsHasOpenAnimation()
    }

    protected fun setIsHasOpenAnimation(value: Boolean){
        sharedPrefUtil.setIsHasOpenAnimation(value)
    }

    protected fun getIsBrightnessActive(): Boolean {
        return sharedPrefUtil.getIsBrightnessActive()
    }

    protected fun setIsBrightnessActive(value: Boolean){
        sharedPrefUtil.setIsBrightnessActive(value)
    }

}