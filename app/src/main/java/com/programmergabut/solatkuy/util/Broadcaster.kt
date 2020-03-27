package com.programmergabut.solatkuy.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class Broadcaster: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val mNotificationHelper = NotificationHelper(context!!)

        val pID = intent?.getIntExtra("prayer_id", -1)
        val pName = intent?.getStringExtra("prayer_name")
        val pTime = intent?.getStringExtra("prayer_time")
        val pCity = intent?.getStringExtra("prayer_city")

        if(pID == -1)
            return

        val nb = mNotificationHelper.getPrayerReminderNC(pName!!,"now is $pTime in $pCity let's pray $pName")
        mNotificationHelper.getManager()?.notify(1, nb.build())
    }

}