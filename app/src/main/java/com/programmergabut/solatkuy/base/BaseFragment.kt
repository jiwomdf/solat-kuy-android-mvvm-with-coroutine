package com.programmergabut.solatkuy.base

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.databinding.LayoutErrorBottomsheetBinding
import com.programmergabut.solatkuy.util.SharedPrefUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

abstract class BaseFragment<out VB : ViewBinding, ViewModelType : ViewModel>(
    private val layout: Int,
    private val clazz: KClass<ViewModelType>,
    private val viewModelTest: ViewModelType?
) : Fragment(), LifecycleObserver {

    val viewModel by viewModel(null, clazz)

    protected val LOCATION_PERMISSIONS = 101

    abstract fun getViewBinding(): VB
    private var _binding: ViewBinding? = null
    protected val binding: VB
        get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflateBinding()
        setListener()
    }

    protected open fun setListener() {}
    protected open fun inflateBinding() {}

    protected fun isLocationPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    protected fun listLocationPermission(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    protected fun <T : Any> gotoIntent(
        classIntent: Class<T>,
        bundle: Bundle? = null,
        isFinish: Boolean = false
    ) {
        val intent = Intent(this.activity, classIntent)
        if (bundle != null)
            intent.putExtras(bundle)
        startActivity(intent)
        if (isFinish)
            activity?.finish()
    }

    protected fun showBottomSheet(
        title: String = resources.getString(R.string.text_error_title),
        description: String = resources.getString(R.string.text_error_dsc),
        isCancelable: Boolean = true,
        isFinish: Boolean = false,
        callback: (() -> Unit)? = null
    ) {

        val dialog = BottomSheetDialog(requireContext())
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
                findNavController().popBackStack()
        }
    }

}