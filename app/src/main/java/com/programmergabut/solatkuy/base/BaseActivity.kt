package com.programmergabut.solatkuy.base

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import kotlinx.android.synthetic.main.layout_error_bottomsheet.*
import javax.inject.Inject

abstract class BaseActivity(
    private val contentView: Int
): AppCompatActivity() {

    @Inject
    lateinit var db : SolatKuyRoom
    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(contentView)
        setIntentExtra()
        setFirstView()
        setObserver()
        setListener()
    }
    abstract fun setIntentExtra()
    abstract fun setFirstView()
    abstract fun setObserver()
    abstract fun setListener()

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

    protected fun showBottomSheet(title : String = resources.getString(R.string.text_error_title), description : String = "",
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

}