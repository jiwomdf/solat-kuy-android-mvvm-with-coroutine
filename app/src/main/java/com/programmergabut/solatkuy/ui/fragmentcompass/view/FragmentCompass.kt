package com.programmergabut.solatkuy.ui.fragmentcompass.view

import android.app.Dialog
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.data.model.entity.MsApi1
import com.programmergabut.solatkuy.ui.fragmentcompass.viewmodel.FragmentCompassViewModel
import com.programmergabut.solatkuy.util.EnumStatus
import com.programmergabut.solatkuy.util.Resource
import com.programmergabut.solatkuy.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_compass.*
import kotlinx.android.synthetic.main.layout_phone_tilt.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/*
 * Created by Katili Jiwo Adi Wiyono on 31/03/20.
 */

class FragmentCompass : Fragment(), SensorEventListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var fragmentCompassViewModel: FragmentCompassViewModel
    private lateinit var mMsApi1: MsApi1

    private var mGravity = FloatArray(3)
    private var mGeomagnetic = FloatArray(3)
    private var azimuth = 0f
    private var currentAzimuth = 0f
    private lateinit var mSensorManager: SensorManager

    private var animationHasOpen: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mSensorManager = activity?.getSystemService(SENSOR_SERVICE) as SensorManager

        fragmentCompassViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(activity?.application!!,
            CoroutineScope(Dispatchers.IO)))[FragmentCompassViewModel::class.java]


        subscribeObserversDB()
        subscribeObserversAPI()

        return inflater.inflate(R.layout.fragment_compass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        refreshLayout()
    }

    /* fetching Prayer Compass */
    private fun fetchCompassApi(msApi1: MsApi1) {
        fragmentCompassViewModel.compassApi.postValue(Resource.loading(null))
        fragmentCompassViewModel.fetchCompassApi(msApi1)
    }

    /* Subscribe live data */
    private fun subscribeObserversAPI() {

        fragmentCompassViewModel.compassApi.observe(this, Observer {retVal ->

            when(retVal.Status){
                EnumStatus.SUCCESS -> {
                    retVal.data?.data.let {
                        tv_qibla_dir.text = it?.direction.toString().substring(0,6).trim() + "°"
                    }}
                EnumStatus.LOADING -> {
                    tv_qibla_dir.text = getString(R.string.loading)
                }
                EnumStatus.ERROR -> tv_qibla_dir.text = getString(R.string.fetch_failed)
            }

        })

    }

    private fun subscribeObserversDB() {
        fragmentCompassViewModel.msApi1Local.observe(this, Observer {
            mMsApi1 = it
            fetchCompassApi(mMsApi1)
        })
    }

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

    override fun onSensorChanged(event: SensorEvent?) {

        if(!animationHasOpen){
            createLottieAnimation()
            animationHasOpen = true
        }


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
            val success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)

            if(success){
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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /* Refresher */
    private fun refreshLayout() {
        sl_compass.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        fetchCompassApi(mMsApi1)
        sl_compass.isRefreshing = false
    }

    /* Lottie Animation */
    private fun createLottieAnimation() {
        val dialogView = layoutInflater.inflate(R.layout.layout_phone_tilt, null)
        val dialog =  Dialog(context!!)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.setContentView(dialogView)
        dialog.show()

        dialog.btn_hideAnimation.setOnClickListener {
            dialog.hide()
        }
    }

}
