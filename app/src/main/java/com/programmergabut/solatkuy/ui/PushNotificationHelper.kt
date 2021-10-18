package com.programmergabut.solatkuy.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import com.programmergabut.solatkuy.broadcaster.PrayerBroadcastReceiver
import com.programmergabut.solatkuy.data.local.localentity.MsNotifiedPrayer
import com.programmergabut.solatkuy.util.Constant
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 02/04/20.
 */

class PushNotificationHelper(
    context: Context,
    prayerListMs: List<MsNotifiedPrayer>,
    cityName: String
) : ContextWrapper(context) {

    private var mCityName: String? = null

    init {
        this.mCityName = cityName

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val newList =
            prayerListMs.filter { x -> x.prayerName != Constant.SUNRISE } //remove sunrise from list
        newList.toMutableList().sortBy { x -> x.prayerID }

        newList.forEach { prayer ->

            val intent = createIntent(context, prayer)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                prayer.prayerID,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            val calendar = createCalendar(prayer)
            if (calendar.before(Calendar.getInstance()))
                calendar.add(Calendar.DATE, 1)

            fireAlarmManager(prayer, pendingIntent, alarmManager, calendar)
        }
    }

    private fun createIntent(context: Context, prayerMs: MsNotifiedPrayer): Intent {
        return Intent(context, PrayerBroadcastReceiver::class.java).also {
            it.putExtra(PrayerBroadcastReceiver.prayerID, prayerMs.prayerID)
            it.putExtra(PrayerBroadcastReceiver.prayerName, prayerMs.prayerName)
            it.putExtra(PrayerBroadcastReceiver.prayerTime, prayerMs.prayerTime)
            it.putExtra(PrayerBroadcastReceiver.prayerCity, mCityName)
        }
    }

    private fun fireAlarmManager(
        prayerMs: MsNotifiedPrayer,
        pendingIntent: PendingIntent,
        alarmManager: AlarmManager,
        calendar: Calendar
    ) {
        if (prayerMs.isNotified) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        } else {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun createCalendar(prayerMs: MsNotifiedPrayer): Calendar {
        val arrPrayer = prayerMs.prayerTime.split(":")
        val hour = arrPrayer[0].trim()
        val minute = arrPrayer[1].split(" ")[0].trim()
        val calendar = Calendar.getInstance()

        calendar.apply {
            set(Calendar.HOUR_OF_DAY, hour.toInt())
            set(Calendar.MINUTE, minute.toInt())
            set(Calendar.SECOND, 0)
        }

        return calendar
    }

}