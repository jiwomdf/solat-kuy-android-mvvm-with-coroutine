package com.programmergabut.solatkuy.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.databinding.LayoutErrorBottomsheetBinding
import com.programmergabut.solatkuy.util.SharedPrefUtil
import javax.inject.Inject

abstract class BaseFragment<DB: ViewDataBinding, VM: ViewModel>(
    private val layout: Int,
    private val viewModelClass: Class<VM>?,
    private val viewModelTest: VM?
) : Fragment() {

    @Inject
    lateinit var db: SolatKuyRoom
    @Inject
    lateinit var sharedPrefUtil: SharedPrefUtil
    lateinit var viewModel: VM
    protected lateinit var binding : DB

    private val ALL_PERMISSIONS = 101


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, layout, container, false)
        binding.lifecycleOwner = this

        viewModelClass?.let {
            viewModel = viewModelTest ?: ViewModelProvider(requireActivity()).get(it)
        }

        setListener()

        return binding.root
    }

    protected open fun setListener(){

    }

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

        val dialogBinding = DataBindingUtil.inflate<LayoutErrorBottomsheetBinding>(
            layoutInflater, R.layout.layout_error_bottomsheet, null, true
        )

        val dialog =  BottomSheetDialog(requireContext())
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(isCancelable)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.tvTitle.text = title
        dialogBinding.tvDesc.text = description
        dialog.show()

        dialogBinding.btnOk.setOnClickListener {
            dialog.hide()

            if(isFinish){
                activity?.finish()
            }
        }
    }

    protected fun getDatabase() = db

    protected fun getLastReadSurah(): Int {
        return sharedPrefUtil.getLastReadSurah()
    }

    protected fun getIsNotHasOpenAnimation(): Boolean {
        return sharedPrefUtil.getIsNotHasOpenAnimation()
    }

    protected fun setIsNotHasOpenAnimation(value: Boolean){
        sharedPrefUtil.setIsNotHasOpenAnimation(value)
    }
}