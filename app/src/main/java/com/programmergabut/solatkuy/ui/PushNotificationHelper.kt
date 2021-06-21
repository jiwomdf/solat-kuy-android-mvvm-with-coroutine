package com.programmergabut.solatkuy.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import com.programmergabut.solatkuy.broadcaster.PrayerBroadcastReceiver
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.service.ServiceUpdateMonthAndYear
import com.programmergabut.solatkuy.util.EnumConfig
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 02/04/20.
 */

class PushNotificationHelper(context: Context, prayerList: List<NotifiedPrayer>, cityName: String): ContextWrapper(context) {

    private var mCityName: String? = null

    init {
        this.mCityName = cityName

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val newList = prayerList.filter { x -> x.prayerName !=  EnumConfig.SUNRISE} //remove sunrise from list
        newList.toMutableList().sortBy { x -> x.prayerID }

        newList.forEach { prayer ->

            val intent = createIntent(context, prayer)
            val pendingIntent = PendingIntent.getBroadcast(context, prayer.prayerID, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            val calendar = createCalendar(prayer)
            if(calendar.before(Calendar.getInstance()))
                calendar.add(Calendar.DATE, 1)

            fireAlarmManager(prayer, pendingIntent, alarmManager, calendar)
        }
    }

    private fun createIntent(context: Context, prayer: NotifiedPrayer): Intent {
        return Intent(context, PrayerBroadcastReceiver::class.java).also {
            it.putExtra(PrayerBroadcastReceiver.prayerID, prayer.prayerID)
            it.putExtra(PrayerBroadcastReceiver.prayerName, prayer.prayerName)
            it.putExtra(PrayerBroadcastReceiver.prayerTime, prayer.prayerTime)
            it.putExtra(PrayerBroadcastReceiver.prayerCity, mCityName)
        }
    }

    private fun fireAlarmManager(
        prayer: NotifiedPrayer,
        pendingIntent: PendingIntent,
        alarmManager: AlarmManager,
        calendar: Calendar
    ) {
        if(prayer.isNotified){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        } else {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun createCalendar(prayer: NotifiedPrayer): Calendar {
        val arrPrayer = prayer.prayerTime.split(":")
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