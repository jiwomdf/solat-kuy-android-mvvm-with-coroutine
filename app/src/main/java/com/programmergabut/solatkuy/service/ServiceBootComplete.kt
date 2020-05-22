package com.programmergabut.solatkuy.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.programmergabut.solatkuy.data.local.SolatKuyRoom
import com.programmergabut.solatkuy.util.helper.PushNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
 * Created by Katili Jiwo Adi Wiyono on 18/04/20.
 */

class ServiceBootComplete: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        super.onStartCommand(intent, flags, startId)

        CoroutineScope(Dispatchers.IO).launch {
            val db = SolatKuyRoom.getDataBase(this@ServiceBootComplete)
            val data = db.notifiedPrayerDao().getNotifiedPrayerSync() as MutableList

            PushNotificationHelper(
                this@ServiceBootComplete,
                data,
                "-"
            )
        }

        return START_REDELIVER_INTENT
    }
}