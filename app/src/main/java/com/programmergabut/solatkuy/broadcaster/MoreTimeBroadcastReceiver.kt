package com.programmergabut.solatkuy.broadcaster

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.programmergabut.solatkuy.util.EnumPrayer
import org.joda.time.LocalTime
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 29/03/20.
 */

class MoreTimeBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val prayerID = intent?.getIntExtra("moreTime_prayerID", -1)
        val originalPrayerTime = intent?.getStringExtra("moreTime_prayerTime")
        val originalPrayerCity = intent?.getStringExtra("moreTime_prayerCity")
        val originalPrayerName = intent?.getStringExtra("moreTime_prayerName")
        val originalListPrayerBundle = intent?.extras?.getBundle("moreTime_listPrayerBundle")


        // new time
        val nowTime = LocalTime.now().toString()
        val hour = nowTime.split(":")[0].trim()
        val minute = nowTime.split(":")[1].trim().toInt() + EnumPrayer.mTime

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hour.toInt())
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, 0)

        // intent
        val i = Intent(context, PrayerBroadcastReceiver::class.java)
        i.putExtra("prayer_name", originalPrayerName)
        i.putExtra("prayer_time", "$hour:$minute")
        i.putExtra("prayer_city", originalPrayerCity)
        i.putExtra("list_prayer_bundle", originalListPrayerBundle)


        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        when(prayerID!!) {
            EnumPrayer.nId1 -> {
                i.putExtra("prayer_id", EnumPrayer.nId2)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.timeInMillis,
                        PendingIntent.getBroadcast(context, EnumPrayer.nId2, i, 0))
                else
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis,
                        PendingIntent.getBroadcast(context, EnumPrayer.nId2, i, 0))

                notificationManager.cancel(EnumPrayer.nId1)
            }
            EnumPrayer.nId2 -> {
                i.putExtra("prayer_id", EnumPrayer.nId1)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.timeInMillis,
                        PendingIntent.getBroadcast(context, EnumPrayer.nId1, i, 0))
                else
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis,
                        PendingIntent.getBroadcast(context, EnumPrayer.nId1, i, 0))

                notificationManager.cancel(EnumPrayer.nId2)
            }
            else -> {
                i.putExtra("prayer_id", EnumPrayer.nId1)
                reloadOriginalIntent(prayerID, originalPrayerName!!, originalPrayerTime!!, originalPrayerCity!!, originalListPrayerBundle!!, context)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.timeInMillis,
                        PendingIntent.getBroadcast(context, EnumPrayer.nId1, i, 0)) //first more time pID = 100
                else
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis,
                        PendingIntent.getBroadcast(context, EnumPrayer.nId1, i, 0)) //first more time pID = 100

                notificationManager.cancel(EnumPrayer.nIdMain)
            }
        }

    }

    private fun reloadOriginalIntent(prayerID: Int, prayerName: String, prayerTime: String, prayerCity: String, originalListPrayerBundle: Bundle,
                                     context: Context? ){

        val iOriginal = Intent(context, PrayerBroadcastReceiver::class.java)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val hour = prayerTime.split(":")[0].trim()
        val minute = prayerTime.split(":")[1].split(" ")[0].trim()

        iOriginal.putExtra("prayer_id", prayerID)
        iOriginal.putExtra("prayer_name", prayerName)
        iOriginal.putExtra("prayer_time", prayerTime)
        iOriginal.putExtra("prayer_city", prayerCity)
        iOriginal.putExtra("list_prayer_bundle", originalListPrayerBundle)

        val c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hour.toInt())
        c.set(Calendar.MINUTE, minute.toInt())
        c.set(Calendar.SECOND, 0)

        if(c.before(Calendar.getInstance()))
            c.add(Calendar.DATE, 1)

        val pendingIntentReload = PendingIntent.getBroadcast(context, prayerID, iOriginal, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntentReload)
        else
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntentReload)
    }

}