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
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.databinding.LayoutErrorBottomsheetBinding
import com.programmergabut.solatkuy.util.SharedPrefUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import javax.inject.Inject
import kotlin.reflect.KClass


abstract class BaseActivity<out VB : ViewBinding, ViewModelType : ViewModel>(
    private val layout: Int,
    private val clazz: KClass<ViewModelType>,
) : AppCompatActivity() {

    lateinit var db: SolatKuyRoom

    lateinit var sharedPrefUtil: SharedPrefUtil

    protected val LOCATION_PERMISSIONS = 101
    val viewModel by viewModel(null,clazz)

    abstract fun getViewBinding(): VB
    private var _binding: ViewBinding? = null
    protected val binding: VB
        get() = _binding as VB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        inflateBinding()
        setListener()
        setContentView(requireNotNull(_binding).root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    protected open fun setListener() {}

    protected open fun inflateBinding() {}

    protected fun isLocationPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    protected fun listLocationPermission(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }

    protected fun <T : Any> gotoIntent(classIntent: Class<T>, bundle: Bundle?, isFinish: Boolean) {
        val intent = Intent(this, classIntent)
        if (bundle != null)
            intent.putExtras(bundle)
        startActivity(intent)
        if (isFinish)
            finish()
    }

    protected fun showBottomSheet(
        title: String = resources.getString(R.string.text_error_title),
        description: String = resources.getString(R.string.text_error_dsc),
        isCancelable: Boolean = true,
        isFinish: Boolean = false,
        callback: (() -> Unit)? = null
    ) {

        val dialog = BottomSheetDialog(this)
        val dialogBinding = LayoutErrorBottomsheetBinding.inflate(layoutInflater)
        dialog.apply {
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCancelable(isCancelable)
            setContentView(dialogBinding.root)
        }
        dialogBinding.apply {
            tvTitle.text = title
            tvDesc.text = description
        }
        dialog.show()
        dialogBinding.btnOk.setOnClickListener {
            dialog.hide()
            callback?.invoke()
            if (isFinish)
                finish()
        }
    }
}