package com.programmergabut.solatkuy.broadcaster

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.programmergabut.solatkuy.room.SolatKuyRoom
import com.programmergabut.solatkuy.util.PushListToNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompleteReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            CoroutineScope(Dispatchers.Default).launch {

                val db = SolatKuyRoom.getDataBase(context!!)
                val data = db.notifiedPrayerDao().getNotifiedPrayerSync() as MutableList

                PushListToNotification(context, data,"-")
            }

        }


    }

}