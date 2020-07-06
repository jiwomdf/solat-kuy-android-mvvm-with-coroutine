package com.programmergabut.solatkuy.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.util.helper.PushNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
 * Created by Katili Jiwo Adi Wiyono on 18/04/20.
 */

class ServiceBootComplete: Service() {

    @Inject lateinit var db: SolatKuyRoom

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        super.onStartCommand(intent, flags, startId)

        CoroutineScope(Dispatchers.IO).launch {

            val data = db.notifiedPrayerDao().getListNotifiedPrayerSync() as MutableList

            PushNotificationHelper(
                this@ServiceBootComplete,
                data,
                "-"
            )
        }

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ServiceBootComplete","Service Destroyed")
    }
}