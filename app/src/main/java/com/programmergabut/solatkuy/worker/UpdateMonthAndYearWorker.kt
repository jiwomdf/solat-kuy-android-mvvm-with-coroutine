package com.programmergabut.solatkuy.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.programmergabut.solatkuy.data.local.dao.MsConfigurationDao
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDate
import java.lang.Exception

class UpdateMonthAndYearWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val msConfigurationDao: MsConfigurationDao
): Worker(context, workerParameters) {

    private val TAG = "UpdateMonthAndYearWorker"

    companion object {
        const val UNIQUE_KEY = "update_month_year_unique_key"
    }

    override fun doWork(): Result {
        return try {
            updateMonthAndYearMsConfiguration()

            Log.e(TAG, "Result.success()")
            Result.success()
        }catch (ex: Exception){
            FirebaseCrashlytics.getInstance().setCustomKey("doWork UpdateMonthAndYearWorker", ex.message.toString())
            Log.e(TAG, ex.message.toString())
            Result.failure()
        }
    }

    private fun updateMonthAndYearMsConfiguration() = runBlocking {
        try {
            val msConfiguration = msConfigurationDao.getMsConfiguration()
            val arrDate = LocalDate.now().toString("dd/M/yyyy").split("/")

            if(msConfiguration == null)
                throw NullPointerException(TAG)

            val year = arrDate[2]
            val month = arrDate[1]
            val dbYear = msConfiguration.year.toInt()
            val dbMoth = msConfiguration.month.toInt()

            if(year.toInt() > dbYear && month.toInt() > dbMoth){
                msConfigurationDao.updateMsConfigurationMonthAndYear(1, arrDate[1], arrDate[2])
                Log.e(TAG, "$year $month $dbYear $dbMoth")
            } else {
                Log.e(TAG, "return@runBlocking")
                return@runBlocking
            }
        }
        catch (ex: Exception){
            Log.d(TAG, ex.message.toString())
        }
    }

}