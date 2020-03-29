package com.programmergabut.solatkuy.util

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*


class MoreTimeBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val prayerID = intent?.getIntExtra("moreTime_prayerID", -1)
        val prayerTime = intent?.getStringExtra("moreTime_prayerTime")
        val prayerCity = intent?.getStringExtra("moreTime_prayerCity")
        val prayerName = intent?.getStringExtra("moreTime_prayerName")

        prayerTime?.let {

            val i = Intent(context, PrayerBroadcastReceiver::class.java)
            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val hour = prayerTime.split(":")[0].trim()
            val minute = prayerTime.split(":")[1].split(" ")[0].trim().toInt() + 3

            val c = Calendar.getInstance()
            c.set(Calendar.HOUR_OF_DAY, hour.toInt())
            c.set(Calendar.MINUTE, minute)
            c.set(Calendar.SECOND, 0)

            i.putExtra("prayer_id", 100)
            i.putExtra("prayer_name", prayerName)
            i.putExtra("prayer_time", minute.toString())
            i.putExtra("prayer_city", prayerCity)

            val pendingIntent = PendingIntent.getBroadcast(context, 100, i, 0)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if(prayerID == 100)
                notificationManager.cancel(100);
            else{
                notificationManager.cancel(prayerID!!);
                reloadOriginalIntent(prayerID, prayerName!!, prayerTime, prayerCity!!, context)
            }

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)

        }
    }

    private fun reloadOriginalIntent(prayerID: Int, prayerName: String, prayerTime: String, prayerCity: String, context: Context? ){

        val iOriginal = Intent(context, PrayerBroadcastReceiver::class.java)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val hour = prayerTime.split(":")[0].trim()
        val minute = prayerTime.split(":")[1].split(" ")[0].trim()


        iOriginal.putExtra("prayer_id", prayerID)
        iOriginal.putExtra("prayer_name", prayerName)
        iOriginal.putExtra("prayer_time", prayerTime)
        iOriginal.putExtra("prayer_city", prayerCity)

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hour.toInt())
        c.set(Calendar.MINUTE, minute.toInt())
        c.set(Calendar.SECOND, 0)

        if(c.before(Calendar.getInstance()))
            c.add(Calendar.DATE, 1)

        val pendingIntentReload = PendingIntent.getBroadcast(context, prayerID, iOriginal, 0)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis, 86400000, pendingIntentReload)
    }

}