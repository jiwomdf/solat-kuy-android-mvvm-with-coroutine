package com.programmergabut.solatkuy.util

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.time.LocalTime
import java.util.*


class MoreTimeBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val prayerID = intent?.getIntExtra("moreTime_prayerID", -1)
        val originalPrayerTime = intent?.getStringExtra("moreTime_prayerTime")
        val originalPrayerCity = intent?.getStringExtra("moreTime_prayerCity")
        val originalPrayerName = intent?.getStringExtra("moreTime_prayerName")

        // new time
        val nowTime = LocalTime.now().toString()
        val hour = nowTime.split(":")[0].trim()
        val minute = nowTime.split(":")[1].trim().toInt() + 6

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hour.toInt())
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, 0)

        // intent
        val i = Intent(context, PrayerBroadcastReceiver::class.java)
        i.putExtra("prayer_name", originalPrayerName)
        i.putExtra("prayer_time", "$hour:$minute")
        i.putExtra("prayer_city", originalPrayerCity)

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        when(prayerID!!) {
            100 -> {
                notificationManager.cancel(100)
                i.putExtra("prayer_id", 200)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.timeInMillis,
                    PendingIntent.getBroadcast(context, 200, i, 0))

            }
            200 -> {
                notificationManager.cancel(200)
                i.putExtra("prayer_id", 100)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.timeInMillis,
                    PendingIntent.getBroadcast(context, 100, i, 0))

            }
            else -> {
                notificationManager.cancel(prayerID)
                i.putExtra("prayer_id", 100)
                reloadOriginalIntent(prayerID, originalPrayerName!!, originalPrayerTime!!, originalPrayerCity!!, context)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.timeInMillis,
                    PendingIntent.getBroadcast(context, 100, i, 0)) //first more time pID = 100
            }
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