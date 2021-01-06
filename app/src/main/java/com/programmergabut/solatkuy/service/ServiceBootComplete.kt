package com.programmergabut.solatkuy.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.util.LogConfig.Companion.ERROR
import com.programmergabut.solatkuy.util.LogConfig.Companion.SERVICE_BOOT_COMPLETE
import com.programmergabut.solatkuy.util.helper.PushNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

/*
 * Created by Katili Jiwo Adi Wiyono on 18/04/20.
 */

class ServiceBootComplete: Service() {

    lateinit var db: SolatKuyRoom

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        db = SolatKuyRoom.getDataBase(this)

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val data = db.notifiedPrayerDao().getListNotifiedPrayer() as MutableList

                PushNotificationHelper(this@ServiceBootComplete, data, "-")
            }
            catch(ex: Exception){
                Log.d(ERROR,"ServiceBootComplete, onStartCommand")
            }
        }

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(SERVICE_BOOT_COMPLETE,"Service Destroyed")
    }
}