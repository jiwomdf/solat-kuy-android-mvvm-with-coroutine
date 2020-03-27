package com.programmergabut.solatkuy.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class Broadcaster: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val mNotificationHelper = NotificationHelper(context!!)


        val nb = mNotificationHelper.getPrayerReminderNC("testing","message")
        mNotificationHelper.getManager()?.notify(1, nb.build())
    }

}