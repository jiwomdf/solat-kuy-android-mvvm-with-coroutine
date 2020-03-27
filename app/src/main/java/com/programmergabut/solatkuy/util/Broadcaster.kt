package com.programmergabut.solatkuy.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.programmergabut.solatkuy.ui.main.view.MainActivity

class Broadcaster: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val mNotificationHelper = NotificationHelper(context!!)

        val pID = intent?.getIntExtra("prayer_id", -1)
        val pName = intent?.getStringExtra("prayer_name")
        val pTime = intent?.getStringExtra("prayer_time")
        val pCity = intent?.getStringExtra("prayer_city")

        if(pID == -1)
            return

        val nextIntent = Intent(context, MainActivity::class.java)
        nextIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, pID!!, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nb = mNotificationHelper.getPrayerReminderNC(pName!!,
            "now is $pTime in $pCity let's pray $pName", pendingIntent)
        mNotificationHelper.getManager()?.notify(1, nb.build())
    }

}