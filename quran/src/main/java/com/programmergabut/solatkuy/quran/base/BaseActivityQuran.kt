package com.programmergabut.solatkuy.quran.base

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.util.SharedPrefUtil
import javax.inject.Inject

abstract class BaseActivityQuran<VB: ViewBinding, VM: ViewModel>(
    private val layout: Int,
    private val viewModelClass: Class<VM>?,
): AppCompatActivity() {

    @Inject
    lateinit var db : SolatKuyRoom
    @Inject
    lateinit var sharedPrefUtil: SharedPrefUtil
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var viewModel: VM

    abstract fun getViewBinding(): VB

    private var _binding: ViewBinding? = null
    protected val binding: VB
        get() = _binding as VB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        viewModelClass?.let {
            viewModel = ViewModelProvider(this, viewModelFactory).get(it)
        }
        setContentView(requireNotNull(_binding).root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }

}