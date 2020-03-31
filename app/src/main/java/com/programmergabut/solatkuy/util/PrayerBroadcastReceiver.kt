package com.programmergabut.solatkuy.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.programmergabut.solatkuy.data.model.entity.PrayerLocal
import com.programmergabut.solatkuy.ui.main.view.MainActivity
import java.io.Serializable
import java.util.*

class PrayerBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val mNotificationHelper = NotificationHelper(context!!)

        val pID = intent?.getIntExtra("prayer_id", -1)
        val pName = intent?.getStringExtra("prayer_name")
        val pTime = intent?.getStringExtra("prayer_time")
        val pCity = intent?.getStringExtra("prayer_city")
        val listData: ArrayList<PrayerLocal>? = intent?.getSerializableExtra("list_data") as ArrayList<PrayerLocal>?

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

        executeNextNotification(listData,context, pCity)
    }


    private fun executeNextNotification(listData: MutableList<PrayerLocal>?, context: Context, pCity: String) {

        val intent = Intent(context, PrayerBroadcastReceiver::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        listData?.forEach{

            val hour = it.prayerTime.split(":")[0].trim()
            val minute = it.prayerTime.split(":")[1].split(" ")[0].trim()

            val c = Calendar.getInstance()
            c.set(Calendar.HOUR_OF_DAY, hour.toInt())
            c.set(Calendar.MINUTE, minute.toInt())
            c.set(Calendar.SECOND, 0)

            intent.putExtra("prayer_id", it.prayerID)
            intent.putExtra("prayer_name", it.prayerName)
            intent.putExtra("prayer_time", it.prayerTime)
            intent.putExtra("prayer_city", pCity)

            val pendingIntent = PendingIntent.getBroadcast(context, it.prayerID, intent, 0)

            if(c.before(Calendar.getInstance()))
                c.add(Calendar.DATE, 1)

            if(it.isNotified)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
            else
                alarmManager.cancel(pendingIntent)
        }

    }

}