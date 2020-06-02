package com.programmergabut.solatkuy.broadcaster

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.programmergabut.solatkuy.service.ServiceBootComplete

/*
 * Created by Katili Jiwo Adi Wiyono on 02/04/20.
 */

class BootCompleteReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            if(context != null) {
               val i = Intent(context, ServiceBootComplete::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    context.startForegroundService(i)
                else
                    context.startService(i)

                /* CoroutineScope(Dispatchers.IO).launch {
                    val db = SolatKuyRoom.getDataBase(context)
                    val data = db.notifiedPrayerDao().getNotifiedPrayerSync() as MutableList

                    PushNotificationHelper(
                        context,
                        data,
                        "-"
                    )
                }*/
            }
            else
                throw Exception("Context Null")

        }

    }

}