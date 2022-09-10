package com.programmergabut.solatkuy.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.programmergabut.solatkuy.data.local.dao.MsConfigurationDao
import com.programmergabut.solatkuy.data.local.dao.MsNotifiedPrayerDao

class MyWorkerFactory(
    private val msNotifiedPrayerDao: MsNotifiedPrayerDao,
    private val msConfigurationDao: MsConfigurationDao
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        return when(workerClassName) {
            FireAlarmManagerWorker::class.java.name ->
                FireAlarmManagerWorker(appContext, workerParameters, msNotifiedPrayerDao, msConfigurationDao)
            UpdateMonthAndYearWorker::class.java.name ->
                UpdateMonthAndYearWorker(appContext, workerParameters, msConfigurationDao)
            else ->
                // Return null, so that the base class can delegate to the default WorkerFactory.
                null
        }

    }
}
