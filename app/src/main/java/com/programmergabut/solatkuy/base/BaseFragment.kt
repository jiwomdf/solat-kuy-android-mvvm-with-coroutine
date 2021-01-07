package com.programmergabut.solatkuy.base

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
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
) : Fragment(), LifecycleObserver {

    @Inject
    lateinit var db: SolatKuyRoom
    @Inject
    lateinit var sharedPrefUtil: SharedPrefUtil
    lateinit var viewModel: VM
    protected lateinit var binding : DB

    protected val LOCATION_PERMISSIONS = 101

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

    protected fun isLocationPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    protected fun listLocationPermission(): Array<String> {
        return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    protected fun <T : Any> gotoIntent(classIntent : Class<T>, bundle : Bundle? = null, isFinish : Boolean = false){
        val intent = Intent(this.activity, classIntent)
        if(bundle != null)
            intent.putExtras(bundle)
        startActivity(intent)
        if(isFinish)
            activity?.finish()
    }

    protected fun showBottomSheet(title : String = resources.getString(R.string.text_error_title),
                                  description : String = resources.getString(R.string.text_error_dsc),
                                  isCancelable : Boolean = true, isFinish : Boolean = false) {

        val dialogBinding: LayoutErrorBottomsheetBinding = DataBindingUtil.inflate(
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
                findNavController().popBackStack()
            }
        }
    }

    protected fun getDatabase() = db

    protected fun getLastReadSurah(): Int {
        return sharedPrefUtil.getLastReadSurah()
    }

    protected fun getIsHasOpenAnimation(): Boolean {
        return sharedPrefUtil.getIsHasOpenAnimation()
    }

    protected fun insertLastReadSharedPref(selectedSurahId: Int, numberInSurah: Int) {
        sharedPrefUtil.insertLastReadSharedPref(selectedSurahId, numberInSurah)
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