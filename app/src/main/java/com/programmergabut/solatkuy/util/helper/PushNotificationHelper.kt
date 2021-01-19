package com.programmergabut.solatkuy.util.helper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.programmergabut.solatkuy.broadcaster.PrayerBroadcastReceiver
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
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

        val newList = selectedList.filter { x -> x.prayerName !=  EnumConfig.SUNRISE} as MutableList<NotifiedPrayer>
        newList.sortBy { x -> x.prayerID }
        newList.forEach { prayer ->

            val arrPrayer = prayer.prayerTime.split(":")
            val hour = arrPrayer[0].trim()
            val minute = arrPrayer[1].split(" ")[0].trim()

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour.toInt())
            calendar.set(Calendar.MINUTE, minute.toInt())
            calendar.set(Calendar.SECOND, 0)

            intent.putExtra(PrayerBroadcastReceiver.PRAYER_ID, prayer.prayerID)
            intent.putExtra(PrayerBroadcastReceiver.PRAYER_NAME, prayer.prayerName)
            intent.putExtra(PrayerBroadcastReceiver.PRAYER_TIME, prayer.prayerTime)
            intent.putExtra(PrayerBroadcastReceiver.PRAYER_CITY, cityName)
            intent.putExtra(PrayerBroadcastReceiver.LIST_PRAYER_BUNDLE, listPrayerBundle)

            val pendingIntent = PendingIntent.getBroadcast(context, prayer.prayerID, intent, 0)

            if(calendar.before(Calendar.getInstance()))
                calendar.add(Calendar.DATE, 1)

            if(prayer.isNotified){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                else
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
            else
                alarmManager.cancel(pendingIntent)

        }

        /*
        * Deprecated, 4 June 2020
        * because the changes of the notification mechanism
        * first fire all the data then cancel all alarm manager when the notification come, then fire it all again
        * also remove the more time feature

        val selPrayer = SelectPrayerHelper.selectNextPrayerToLocalPrayer(selList)
        //val selPrayer = NotifiedPrayer(1,"Isha", true, "18:47") //#testing purpose

        selPrayer?.let{

            val arrPrayer = it.prayerTime.split(":")

            val hour = arrPrayer[0].trim()
            val minute = arrPrayer[1].split(" ")[0].trim()

            val c = Calendar.getInstance()
            c.set(Calendar.HOUR_OF_DAY, hour.toInt())
            c.set(Calendar.MINUTE, minute.toInt())
            c.set(Calendar.SECOND, 0)

            intent.putExtra("prayer_id", it.prayerID)
            intent.putExtra("prayer_name", it.prayerName)
            intent.putExtra("prayer_time", it.prayerTime)
            intent.putExtra("prayer_city", mCityName)
            intent.putExtra("list_prayer_bundle", listPrayerBundle)

            val pendingIntent = PendingIntent.getBroadcast(context, EnumConfig.nIdMain, intent, 0)

            if(c.before(Calendar.getInstance()))
                c.add(Calendar.DATE, 1)

            if(it.isNotified)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
                else
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
            else
                alarmManager.cancel(pendingIntent)
        } */
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

            if(it.isNotified)
                listPrayerIsNotified.add(1)
            else
                listPrayerIsNotified.add(0)

            if(mCityName.isNullOrEmpty())
                listPrayerCity.add("-")
            else
                listPrayerCity.add(mCityName!!)
        }

        val bundle = Bundle()
        bundle.putIntegerArrayList(PrayerBroadcastReceiver.LIST_PRAYER_ID, listPrayerID)
        bundle.putStringArrayList(PrayerBroadcastReceiver.LIST_PRAYER_NAME, listPrayerName)
        bundle.putStringArrayList(PrayerBroadcastReceiver.LIST_PRAYER_TIME, listPrayerTime)
        bundle.putIntegerArrayList(PrayerBroadcastReceiver.LIST_PRAYER_IS_NOTIFIED, listPrayerIsNotified)
        bundle.putStringArrayList(PrayerBroadcastReceiver.LIST_PRAYER_CITY, listPrayerCity)

        return bundle
    }

}