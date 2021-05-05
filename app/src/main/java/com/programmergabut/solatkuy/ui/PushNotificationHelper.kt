package com.programmergabut.solatkuy.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.programmergabut.solatkuy.broadcaster.PrayerBroadcastReceiver
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.model.PrayerExtraData
import com.programmergabut.solatkuy.model.PrayerListExtraData
import com.programmergabut.solatkuy.service.ServiceUpdateMonthAndYear
import com.programmergabut.solatkuy.util.EnumConfig
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 02/04/20.
 */

class PushNotificationHelper(context: Context, selectedList: List<NotifiedPrayer>, cityName: String): ContextWrapper(context) {

    private var mCityName: String? = null

    init {
        this.mCityName = cityName

        val intent = Intent(context, PrayerBroadcastReceiver::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val listPrayerBundle = bundleCreator(selectedList)
        val newList = selectedList.filter { x -> x.prayerName !=  EnumConfig.SUNRISE} //remove sunrise from list
        newList.toMutableList().sortBy { x -> x.prayerID }

        newList.forEach { prayer ->

            intent.putExtra(PrayerBroadcastReceiver.PrayerData, PrayerExtraData(
                prayerId = prayer.prayerID,
                prayerName = prayer.prayerName,
                prayerTime = prayer.prayerTime,
                prayerCity = cityName,
                listPrayerBundle = listPrayerBundle
            ))

            val pendingIntent = PendingIntent.getBroadcast(context, prayer.prayerID, intent, 0)
            val calendar = createCalendar(prayer)
            if(calendar.before(Calendar.getInstance()))
                calendar.add(Calendar.DATE, 1)

            fireAlarmManager(prayer, pendingIntent, alarmManager, calendar)
        }

        startUpdateMonthYearService(context)
    }

    private fun fireAlarmManager(
        prayer: NotifiedPrayer,
        pendingIntent: PendingIntent,
        alarmManager: AlarmManager,
        calendar: Calendar
    ) {
        if(prayer.isNotified){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            else
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
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

    private fun startUpdateMonthYearService(context: Context) {
        val serviceIntent = Intent(context, ServiceUpdateMonthAndYear::class.java)
        context.startService(serviceIntent)
    }

    private fun bundleCreator(selectedList: List<NotifiedPrayer>): Bundle {
        val listPrayerID = arrayListOf<Int>()
        val listPrayerName = arrayListOf<String>()
        val listPrayerTime = arrayListOf<String>()
        val listPrayerIsNotified = arrayListOf<Int>()
        val listPrayerCity = arrayListOf<String>()

        selectedList.forEach {
            listPrayerID.add(it.prayerID)
            listPrayerName.add(it.prayerName)
            listPrayerTime.add(it.prayerTime)
            if(it.isNotified) listPrayerIsNotified.add(1) else listPrayerIsNotified.add(0)
            if(mCityName.isNullOrEmpty()) listPrayerCity.add("-") else listPrayerCity.add(mCityName!!)
        }

        val bundle = Bundle()
        bundle.putParcelable(PrayerBroadcastReceiver.PrayerListData, PrayerListExtraData(
            listPrayerID = listPrayerID,
            listPrayerName = listPrayerName,
            listPrayerTime = listPrayerTime,
            listPrayerIsNotified = listPrayerIsNotified,
            listPrayerCity = listPrayerCity,
        ))
        return bundle
    }

}