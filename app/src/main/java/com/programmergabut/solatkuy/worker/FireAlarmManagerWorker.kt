package com.programmergabut.solatkuy.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.FirebaseCrashlytics

class FireAlarmManagerWorker(
    val context: Context,
    workerParameters: WorkerParameters,
    private val msNotifiedPrayerDao: MsNotifiedPrayerDao,
    private val msConfigurationDao: MsConfigurationDao
): Worker(context, workerParameters) {

    private val TAG = "FireAlarmManagerWorker"

    companion object {
        const val UNIQUE_KEY = "fire_alarm_manager_unique_key"
    }

    override fun doWork(): Result {
        return try {
            val api1 = msConfigurationDao.getMsConfiguration()
            val cityName = if(api1?.latitude == null || api1?.longitude == null){
                "-"
            } else {
                LocationHelper.getCity(context, api1.latitude.toDouble(), api1.longitude.toDouble()) ?: "-"
            }
            val listPrayer = msNotifiedPrayerDao.getListNotifiedPrayerSync()

            PushNotificationHelper(context, listPrayer, cityName)

            Log.e(TAG, cityName)
            Log.e(TAG, listPrayer.toString())

            Result.success()
        }catch (ex: Exception){
            FirebaseCrashlytics.getInstance().setCustomKey("doWork FireAlarmManagerWorker", ex.message.toString())
            Log.e(TAG, ex.message.toString())
            Result.failure()
        }
    }



}