package com.programmergabut.solatkuy.ui.fragmentcompass.view

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
import com.programmergabut.solatkuy.R
import kotlinx.android.synthetic.main.fragment_prayer_report.*

/*
 * Created by Katili Jiwo Adi Wiyono on 31/03/20.
 */

class FragmentCompass : Fragment(), SensorEventListener {

    private var mGravity = FloatArray(3)
    private var mGeomagnetic = FloatArray(3)
    private var azimuth = 0f
    private var currentAzimuth = 0f
    lateinit var mSensorManager: SensorManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mSensorManager = activity?.getSystemService(SENSOR_SERVICE) as SensorManager


        return inflater.inflate(R.layout.fragment_prayer_report, container, false)
    }

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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

}
