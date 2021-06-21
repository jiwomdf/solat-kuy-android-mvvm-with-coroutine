package com.programmergabut.solatkuy.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import java.lang.Exception

class ServiceUpdateMonthAndYear: Service() {

    private val TAG = "ServiceUpdateMonthAndYear"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        CoroutineScope(Dispatchers.IO).launch {
            updateMonthAndYearMsApi1()
        }
        return START_REDELIVER_INTENT
    }

    private suspend fun updateMonthAndYearMsApi1() {
        try {
            val db = SolatKuyRoom.getDataBase(this@ServiceUpdateMonthAndYear)
            val msApi1 = db.msApi1Dao().getMsApi1()
            val arrDate = LocalDate.now().toString("dd/M/yyyy").split("/")

            if(msApi1 == null)
                throw NullPointerException("ServiceUpdateMonthAndYear updateMonthAndYearMsApi1 data == null")

            val year = arrDate[2]
            val month = arrDate[1]
            val dbYear = msApi1.year.toInt()
            val dbMoth = msApi1.month.toInt()

            if(year.toInt() > dbYear && month.toInt() > dbMoth){
                db.msApi1Dao().updateMsApi1MonthAndYear(1, arrDate[1], arrDate[2])
            }
        }
        catch (ex: Exception){
            Log.d(TAG, ex.message.toString())
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"Service Destroyed")
    }
}