package com.programmergabut.solatkuy.ui.main.qibla

import android.app.Dialog
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.programmergabut.solatkuy.R
import com.programmergabut.solatkuy.base.BaseFragment
import com.programmergabut.solatkuy.data.remote.json.compassJson.Result
import com.programmergabut.solatkuy.databinding.FragmentCompassBinding
import com.programmergabut.solatkuy.databinding.LayoutPhoneTiltBinding
import com.programmergabut.solatkuy.util.EnumStatus

class CompassFragment constructor(
    viewModelTest: CompassViewModel? = null
) : BaseFragment<FragmentCompassBinding, CompassViewModel>(
    R.layout.fragment_compass,
    CompassViewModel::class,
    viewModelTest
), SensorEventListener, SwipeRefreshLayout.OnRefreshListener {

    private var azimuth = 0f
    private var currentAzimuth = 0f
    private var mGravity = FloatArray(3)
    private var mGeomagnetic = FloatArray(3)
    private lateinit var mSensorManager: SensorManager

    override fun getViewBinding() = FragmentCompassBinding.inflate(layoutInflater)

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(
            this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_GAME
        )
        mSensorManager.registerListener(
            this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSensorManager = activity?.getSystemService(SENSOR_SERVICE) as SensorManager
        openLottieAnimation()
    }

    override fun setListener() {
        super.setListener()
        binding.slCompass.setOnRefreshListener(this)
        subscribeObserversDB()
        subscribeObserversAPI()
    }

    private fun openLottieAnimation() {
        val isHasOpenAnimation = viewModel.sharedPrefUtil.getIsHasOpenAnimation()
        if (!isHasOpenAnimation)
            createLottieAnimation()
    }

    private fun subscribeObserversAPI() {
        viewModel.compass.observe(viewLifecycleOwner, { retVal ->
            when (retVal.status) {
                EnumStatus.SUCCESS -> {
                    if (retVal.data == null) {
                        showBottomSheet()
                        return@observe
                    }
                    binding.tvQiblaDir.text = shortenTextDegree(retVal.data.data)
                }
                EnumStatus.LOADING -> binding.tvQiblaDir.text = getString(R.string.loading)
                EnumStatus.ERROR -> binding.tvQiblaDir.text = getString(R.string.fetch_failed)
            }
        })
    }

    private fun shortenTextDegree(data: Result): String {
        return if (data.direction.toString().length > 6)
            data.direction.toString().substring(0, 6).trim() + "°"
        else
            data.direction.toString()
    }

    private fun subscribeObserversDB() {
        viewModel.msApi1.observe(viewLifecycleOwner, {
            if (it == null) {
                showBottomSheet(isCancelable = false, isFinish = true)
                return@observe
            }
            viewModel.fetchCompassApi(it)
        })
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val alpha = 0.97f
        synchronized(this) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0]
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1]
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2]
            }
            if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0]
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1]
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2]
            }
            val R = FloatArray(9)
            val I = FloatArray(9)
            val isSuccess = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)
            if (isSuccess) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuth = (azimuth + 360) % 360
                val anim = RotateAnimation(
                    -currentAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5F
                )
                currentAzimuth = azimuth
                anim.duration = 500
                anim.repeatCount = 0
                anim.fillAfter = true
                binding.ivCompass.startAnimation(anim)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        when (accuracy) {
            0 -> {
                binding.tvQiblaAccuracy.text = getString(R.string.unreliable)
                binding.tvQiblaAccuracy.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red_700
                    )
                )
            }
            1 -> {
                binding.tvQiblaAccuracy.text = getString(R.string.lowAccuracy)
                binding.tvQiblaAccuracy.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red_200
                    )
                )
            }
            2 -> {
                binding.tvQiblaAccuracy.text = getString(R.string.mediumAccuracy)
                binding.tvQiblaAccuracy.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue_500
                    )
                )
            }
            3 -> {
                binding.tvQiblaAccuracy.text = getString(R.string.highAccuracy)
                binding.tvQiblaAccuracy.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
            }
        }
    }

    override fun onRefresh() {
        if (viewModel.msApi1.value == null)
            return
        viewModel.fetchCompassApi(viewModel.msApi1.value!!)
        binding.slCompass.isRefreshing = false
    }

    private fun createLottieAnimation() {
        val dialog = Dialog(requireContext())
        val dialogBinding = LayoutPhoneTiltBinding.inflate(layoutInflater)
        dialog.apply {
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCancelable(false)
            setContentView(dialogBinding.root)
            show()
        }
        dialogBinding.btnHideAnimation.setOnClickListener {
            viewModel.sharedPrefUtil.setIsHasOpenAnimation(true)
            dialog.hide()
        }
    }

}
