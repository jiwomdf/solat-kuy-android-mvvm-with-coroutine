package com.programmergabut.solatkuy.broadcaster

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.programmergabut.solatkuy.data.room.SolatKuyRoom
import com.programmergabut.solatkuy.service.ServiceBootComplete
import com.programmergabut.solatkuy.util.PushNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

/*
 * Created by Katili Jiwo Adi Wiyono on 02/04/20.
 */

class BootCompleteReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            if(context != null) {
               val i = Intent(context, ServiceBootComplete::class.java)
               context.startForegroundService(i)
            }
            else
                throw Exception("Context Null")

        }

    }

}