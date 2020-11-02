package com.programmergabut.solatkuy.base

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_error_bottomsheet.*
import javax.inject.Inject

abstract class BaseFragment(fragmentLayout: Int) : Fragment(fragmentLayout) {

    @Inject
    lateinit var db: SolatKuyRoom
    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setIntentExtra()
        setFirstView()
        setObserver()
        setListener()
    }
    abstract fun setIntentExtra()
    abstract fun setFirstView()
    abstract fun setObserver()
    abstract fun setListener()

    protected fun <T : Any> gotoIntent(classIntent : Class<T>, bundle : Bundle? = null, isFinish : Boolean = false){
        val intent = Intent(this.activity, classIntent)
        if(bundle != null)
            intent.putExtras(bundle)
        startActivity(intent)
        if(isFinish)
            activity?.finish()
    }

    protected fun showBottomSheet(title : String = resources.getString(R.string.text_error_title), description : String = "",
                                  isCancelable : Boolean = true, isFinish : Boolean = false) {

        val dialogView = layoutInflater.inflate(R.layout.layout_error_bottomsheet, null)
        val dialog =  BottomSheetDialog(requireContext())
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(isCancelable)
        dialog.setContentView(dialogView)
        dialog.tv_title.text = title
        dialog.tv_desc.text = description
        dialog.show()

        dialog.btn_ok.setOnClickListener {
            dialog.hide()

            /* if(isFinish){
                val controller = navHostFragment.findNavController()
                controller.popBackStack(R.id.fragmentMain, true)
            } */
        }
    }

}