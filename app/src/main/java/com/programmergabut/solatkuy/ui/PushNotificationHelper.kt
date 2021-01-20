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
import com.programmergabut.solatkuy.service.ServiceBootComplete
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
            val arrPrayer = prayer.prayerTime.split(":")
            val hour = arrPrayer[0].trim()
            val minute = arrPrayer[1].split(" ")[0].trim()
            val calendar = Calendar.getInstance()

            calendar.apply {
                set(Calendar.HOUR_OF_DAY, hour.toInt())
                set(Calendar.MINUTE, minute.toInt())
                set(Calendar.SECOND, 0)
            }
            intent.apply {
                putExtra(PrayerBroadcastReceiver.PRAYER_ID, prayer.prayerID)
                putExtra(PrayerBroadcastReceiver.PRAYER_NAME, prayer.prayerName)
                putExtra(PrayerBroadcastReceiver.PRAYER_TIME, prayer.prayerTime)
                putExtra(PrayerBroadcastReceiver.PRAYER_CITY, cityName)
                putExtra(PrayerBroadcastReceiver.LIST_PRAYER_BUNDLE, listPrayerBundle)
            }

            val pendingIntent = PendingIntent.getBroadcast(context, prayer.prayerID, intent, 0)
            if(calendar.before(Calendar.getInstance()))
                calendar.add(Calendar.DATE, 1)

            if(prayer.isNotified){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                else
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.cancel(pendingIntent)
            }
        }

        startUpdateMonthYearService(context)
    }

    private fun startUpdateMonthYearService(context: Context) {
        val serviceIntent = Intent(context, ServiceUpdateMonthAndYear::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startService(serviceIntent)
        else
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
        bundle.apply {
            putIntegerArrayList(PrayerBroadcastReceiver.LIST_PRAYER_ID, listPrayerID)
            putStringArrayList(PrayerBroadcastReceiver.LIST_PRAYER_NAME, listPrayerName)
            putStringArrayList(PrayerBroadcastReceiver.LIST_PRAYER_TIME, listPrayerTime)
            putIntegerArrayList(PrayerBroadcastReceiver.LIST_PRAYER_IS_NOTIFIED, listPrayerIsNotified)
            putStringArrayList(PrayerBroadcastReceiver.LIST_PRAYER_CITY, listPrayerCity)
        }
        return bundle
    }

}