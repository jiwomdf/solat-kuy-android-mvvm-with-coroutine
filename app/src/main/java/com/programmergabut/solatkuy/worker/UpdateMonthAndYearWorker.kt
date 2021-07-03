package com.programmergabut.solatkuy.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.programmergabut.solatkuy.data.local.dao.MsApi1Dao
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDate
import java.lang.Exception

class UpdateMonthAndYearWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
    private val msApi1Dao: MsApi1Dao
): Worker(context, workerParameters) {

    private val TAG = "UpdateMonthAndYearWorker"

    companion object {
        const val UNIQUE_KEY = "update_month_year_unique_key"
    }

    override fun doWork(): Result {
        return try {
            updateMonthAndYearMsApi1()

            Log.e(TAG, "Result.success()")
            Result.success()
        }catch (ex: Exception){
            Log.e(TAG, ex.message.toString())
            Result.failure()
        }
    }

    private fun updateMonthAndYearMsApi1() = runBlocking {
        try {
            val msApi1 = msApi1Dao.getMsApi1()
            val arrDate = LocalDate.now().toString("dd/M/yyyy").split("/")

            if(msApi1 == null)
                throw NullPointerException(TAG)

            val year = arrDate[2]
            val month = arrDate[1]
            val dbYear = msApi1.year.toInt()
            val dbMoth = msApi1.month.toInt()

            if(year.toInt() > dbYear && month.toInt() > dbMoth){
                msApi1Dao.updateMsApi1MonthAndYear(1, arrDate[1], arrDate[2])
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