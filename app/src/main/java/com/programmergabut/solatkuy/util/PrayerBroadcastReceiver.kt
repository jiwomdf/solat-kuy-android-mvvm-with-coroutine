package com.programmergabut.solatkuy.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.programmergabut.solatkuy.ui.main.view.MainActivity

class PrayerBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val mNotificationHelper = NotificationHelper(context!!)

        val pID = intent?.getIntExtra("prayer_id", -1)
        val pName = intent?.getStringExtra("prayer_name")
        val pTime = intent?.getStringExtra("prayer_time")
        val pCity = intent?.getStringExtra("prayer_city")

        if(pID == -1 && !pName.isNullOrEmpty() && !pTime.isNullOrEmpty())
            return

        val nextIntent = Intent(context, MainActivity::class.java)
        nextIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK


        when (pID!!) {
            100 -> {
                val pendingIntent = PendingIntent.getActivity(context, 100,
                    nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val nb = mNotificationHelper.getPrayerReminderNC(100, pTime!!, pCity!!, pName!!, pendingIntent)
                mNotificationHelper.getManager()?.notify(100, nb.build())
            }
            200 -> {
                val pendingIntent = PendingIntent.getActivity(context, 200, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val nb = mNotificationHelper.getPrayerReminderNC(200, pTime!!, pCity!!, pName!!, pendingIntent)
                mNotificationHelper.getManager()?.notify(200, nb.build())
            }
            else -> {
                val pendingIntent = PendingIntent.getActivity(context, pID, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val nb = mNotificationHelper.getPrayerReminderNC(pID, pTime!!, pCity!!, pName!!, pendingIntent)
                mNotificationHelper.getManager()?.notify(pID, nb.build())
            }
        }

    }

}