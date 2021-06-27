package com.programmergabut.solatkuy.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.programmergabut.solatkuy.data.local.dao.MsApi1Dao
import com.programmergabut.solatkuy.data.local.dao.MsNotifiedPrayerDao

class MyWorkerFactory(
    private val msNotifiedPrayerDao: MsNotifiedPrayerDao,
    private val msApi1Dao: MsApi1Dao
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        return when(workerClassName) {
            FireAlarmManagerWorker::class.java.name ->
                FireAlarmManagerWorker(appContext, workerParameters, msNotifiedPrayerDao, msApi1Dao)
            UpdateMonthAndYearWorker::class.java.name ->
                UpdateMonthAndYearWorker(appContext, workerParameters, msApi1Dao)
            else ->
                // Return null, so that the base class can delegate to the default WorkerFactory.
                null
        }

    }
}
