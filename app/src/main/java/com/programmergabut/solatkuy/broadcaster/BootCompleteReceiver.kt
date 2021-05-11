package com.programmergabut.solatkuy.broadcaster

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.programmergabut.solatkuy.service.ServiceUpdateMonthAndYear
import com.programmergabut.solatkuy.worker.FireAlarmManagerWorker
import com.programmergabut.solatkuy.worker.UpdateMonthAndYearWorker
import java.util.concurrent.TimeUnit

/*
 * Created by Katili Jiwo Adi Wiyono on 02/04/20.
 */

class BootCompleteReceiver: BroadcastReceiver() {

    private val TAG = "BootCompleteReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            try {
                if(context != null) {
                    fireAlarmManagerWorker(context)
                    fireUpdateMonthYearWorker(context)
                    Log.e(TAG, "MASUK BootCompleteReceiver")
                } else {
                    Log.e(TAG, "ERROR context != null")
                    throw Exception()
                }

                startUpdateMonthYearService(context)
            }catch (ex: Exception){
                Log.e(TAG, ex.message.toString())
                throw Exception(ex.message)
            }
        }
    }

    private fun fireAlarmManagerWorker(context: Context) {
        val task = PeriodicWorkRequest.Builder(FireAlarmManagerWorker::class.java, 60, TimeUnit.MINUTES)
            .build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniquePeriodicWork(FireAlarmManagerWorker.UNIQUE_KEY, ExistingPeriodicWorkPolicy.KEEP, task)
    }

    private fun fireUpdateMonthYearWorker(context: Context) {
        val task = PeriodicWorkRequest.Builder(UpdateMonthAndYearWorker::class.java, 720, TimeUnit.MINUTES)
            .build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniquePeriodicWork(UpdateMonthAndYearWorker.UNIQUE_KEY, ExistingPeriodicWorkPolicy.KEEP, task)
    }

    private fun startUpdateMonthYearService(context: Context) {
        val serviceIntent = Intent(context, ServiceUpdateMonthAndYear::class.java)
        context.startService(serviceIntent)
    }
}