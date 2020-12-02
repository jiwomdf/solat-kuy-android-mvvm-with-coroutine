package com.programmergabut.solatkuy.ui.fragmentcompass

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseFragment
import com.programmergabut.solatkuy.data.local.localentity.MsApi1
import com.programmergabut.solatkuy.util.enumclass.EnumStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_compass.*
import kotlinx.android.synthetic.main.layout_phone_tilt.*

/*
 * Created by Katili Jiwo Adi Wiyono on 31/03/20.
 */

@AndroidEntryPoint
class FragmentCompass : BaseFragment(R.layout.fragment_compass), SensorEventListener, SwipeRefreshLayout.OnRefreshListener {

    private val fragmentCompassViewModel: FragmentCompassViewModel by viewModels()
    private lateinit var mMsApi1: MsApi1

    private var mGravity = FloatArray(3)
    private var mGeomagnetic = FloatArray(3)
    private var azimuth = 0f
    private var currentAzimuth = 0f
    private lateinit var mSensorManager: SensorManager

    /* Compass */
    override fun onResume() {
        super.onResume()

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_GAME)
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    override fun setFirstView() {
        mSensorManager = activity?.getSystemService(SENSOR_SERVICE) as SensorManager
        openLottieAnimation()
    }
    override fun setObserver() {
        subscribeObserversDB()
        subscribeObserversAPI()
    }
    override fun setListener() {
        sl_compass.setOnRefreshListener(this)
    }

    private fun openLottieAnimation() {
        val isHasNotOpenAnimation = getIsNotHasOpenAnimation()
        if(isHasNotOpenAnimation)
            createLottieAnimation()
    }

    private fun saveSharedPreferences() {

    }

    /* Subscribe live data */
    private fun subscribeObserversAPI() {
        fragmentCompassViewModel.compass.observe(viewLifecycleOwner, Observer { retVal ->
            when(retVal.status){
                EnumStatus.SUCCESS -> {
                    retVal.data?.data.let {
                        tv_qibla_dir.text = if(it?.direction.toString().length > 6)
                            it?.direction.toString().substring(0,6).trim() + "°"
                        else
                            it?.direction.toString()
                    }}
                EnumStatus.LOADING -> {
                    tv_qibla_dir.text = getString(R.string.loading)
                }
                EnumStatus.ERROR -> tv_qibla_dir.text = getString(R.string.fetch_failed)
            }
        })
    }

    private fun subscribeObserversDB() {
        fragmentCompassViewModel.msApi1.observe(viewLifecycleOwner, Observer {
            when(it.status){
                EnumStatus.SUCCESS -> {
                    if(it.data == null)
                        throw Exception("MsApi1 for Compass Null")

                    mMsApi1 = it.data
                    fragmentCompassViewModel.fetchCompassApi(it.data)
                }
                EnumStatus.LOADING -> {}
                EnumStatus.ERROR -> {}
            }
        })
    }

    override fun onSensorChanged(event: SensorEvent?) {

        val alpha = 0.97f
        synchronized(this){

            if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0]
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1]
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2]
            }

            if(event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD){
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0]
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1]
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2]
            }

            val R = FloatArray(9)
            val I = FloatArray(9)
            val isSuccess = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)

            if(isSuccess){
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuth = (azimuth + 360) % 360

                val anim = RotateAnimation(-currentAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5F)

                currentAzimuth = azimuth

                anim.duration = 500
                anim.repeatCount = 0
                anim.fillAfter = true

                iv_compass.startAnimation(anim)
            }

        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {/*NO-OP*/}

    override fun onRefresh() {
        fragmentCompassViewModel.fetchCompassApi(mMsApi1)
        sl_compass.isRefreshing = false
    }

    /* Lottie Animation */
    private fun createLottieAnimation() {
        val dialogView = layoutInflater.inflate(R.layout.layout_phone_tilt, null)
        val dialog =  Dialog(requireContext())
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()

        dialog.btn_hideAnimation.setOnClickListener {
            saveSharedPreferences()
            dialog.hide()
        }
    }

}
