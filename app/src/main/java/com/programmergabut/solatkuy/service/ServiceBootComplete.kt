package com.programmergabut.solatkuy.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.ui.PushNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.NullPointerException

/*
 * Created by Katili Jiwo Adi Wiyono on 18/04/20.
 */

class ServiceBootComplete: Service() {

    private val TAG = "ServiceBootComplete"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val db = SolatKuyRoom.getDataBase(this@ServiceBootComplete)
                val data = db.notifiedPrayerDao().getListNotifiedPrayer()
                if(data != null){
                    PushNotificationHelper(this@ServiceBootComplete, data.value!!.toMutableList(), "-")
                } else {
                    throw NullPointerException("ServiceBootComplete onStartCommand data == null")
                }
            }
            catch(ex: Exception){
                Log.d(TAG, ex.message.toString())
            }
        }
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"Service Destroyed")
    }
}