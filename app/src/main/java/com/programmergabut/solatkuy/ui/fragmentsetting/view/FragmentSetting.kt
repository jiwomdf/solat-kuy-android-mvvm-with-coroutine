package com.programmergabut.solatkuy.ui.fragmentsetting.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.model.MsApi1
import com.programmergabut.solatkuy.ui.fragmentsetting.viewmodel.FragmentSettingViewModel
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.layout_bottomsheet_bycity.view.*
import kotlinx.android.synthetic.main.layout_bottomsheet_bygps.view.*
import kotlinx.android.synthetic.main.layout_bottomsheet_bylatitudelongitude.view.*

class FragmentSetting : Fragment() {

    private lateinit var dialog: BottomSheetDialog
    lateinit var dialogView: View
    private lateinit var fragmentSettingViewModel: FragmentSettingViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = BottomSheetDialog(context!!)
        rbSelection()

        fragmentSettingViewModel = ViewModelProviders.of(requireActivity()).get(FragmentSettingViewModel::class.java)
        subscribeObserversDB()
    }

    private fun rbSelection(){
        rb_byLatitudeLongitude.setOnClickListener {
            dialogView = layoutInflater.inflate(R.layout.layout_bottomsheet_bylatitudelongitude,null)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false)
            dialog.setContentView(dialogView)
            dialog.show()

            dialogView.btn_proceedByLL.setOnClickListener{

                insertLocationSettingToDb(dialogView)

                dialog.dismiss()
            }
        }

        rb_byGps.setOnClickListener{
            dialogView = layoutInflater.inflate(R.layout.layout_bottomsheet_bygps,null)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false)
            dialog.setContentView(dialogView)
            dialog.show()

            dialogView.btn_proceedByGps.setOnClickListener{
                dialog.dismiss()
            }
        }

        rb_byCity.setOnClickListener{
            dialogView = layoutInflater.inflate(R.layout.layout_bottomsheet_bycity,null)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false)
            dialog.setContentView(dialogView)
            dialog.show()

            dialogView.btn_proceedByCity.setOnClickListener{
                dialog.dismiss()
            }

            bindRbCityData(dialogView)
        }
    }

    private fun insertLocationSettingToDb(dialogView: View) {

        val data = MsApi1(1,
            dialogView.et_llDialog_latitude.text.toString().trim(),
            dialogView.et_llDialog_longitude.text.toString().trim(),
            "8","3","2020")

        fragmentSettingViewModel.updateMsApi1(data)
    }

    private fun subscribeObserversDB() {
        fragmentSettingViewModel.msApi1Local.observe(this, Observer {
            tv_view_latitude.text = it.latitude
            tv_view_longitude.text = it.longitude
            tv_view_city.text = "-"
        })
    }

    private fun bindRbCityData(dialogView: View){
        val country = arrayOf("Indonesia", "Korea")
        val city = arrayOf("Jakarta", "Solo", "Busan")

        val countryAdapter = ArrayAdapter<Any?>(context!!, android.R.layout.simple_spinner_dropdown_item, country)
        val cityAdapter = ArrayAdapter<Any?>(context!!, android.R.layout.simple_spinner_dropdown_item, city)

        dialogView.s_selCountry.adapter = countryAdapter
        dialogView.s_selCity.adapter = cityAdapter
    }


}
