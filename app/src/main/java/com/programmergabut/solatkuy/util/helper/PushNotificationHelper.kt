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

class PushNotificationHelper(context: Context, selList: MutableList<NotifiedPrayer>, mCityName: String): ContextWrapper(context) {

    private var mCityName: String? = null

    init {
        this.mCityName = mCityName

        val intent = Intent(context, PrayerBroadcastReceiver::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val listPrayerBundle = bundleCreator(selList)

        val newList = selList.filter { x -> x.prayerName !=  EnumConfig.SUNRISE} as MutableList<NotifiedPrayer>

        newList.sortBy { x -> x.prayerID }
        newList.forEachIndexed { _, it ->

            val arrPrayer = it.prayerTime.split(":")
            val hour = arrPrayer[0].trim()
            val minute = arrPrayer[1].split(" ")[0].trim()

            val c = Calendar.getInstance()
            c.set(Calendar.HOUR_OF_DAY, hour.toInt())
            c.set(Calendar.MINUTE, minute.toInt())
            c.set(Calendar.SECOND, 0)

            intent.putExtra(PrayerBroadcastReceiver.prayer_id, it.prayerID)
            intent.putExtra(PrayerBroadcastReceiver.prayer_name, it.prayerName)
            intent.putExtra(PrayerBroadcastReceiver.prayer_time, it.prayerTime)
            intent.putExtra(PrayerBroadcastReceiver.prayer_city, mCityName)
            intent.putExtra(PrayerBroadcastReceiver.list_prayer_bundle, listPrayerBundle)

            val pendingIntent = PendingIntent.getBroadcast(context, it.prayerID, intent, 0)

            if(c.before(Calendar.getInstance()))
                c.add(Calendar.DATE, 1)

            if(it.isNotified){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
                else
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
            }
            else
                alarmManager.cancel(pendingIntent)

        }

        /*
        * Deprecated, 4 June 2020
        * because change the notification mechanism to fire all the data
        * then cancel all alarm manager when the notification come, then fire it all again
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

   private fun bundleCreator(selList: MutableList<NotifiedPrayer>): Bundle {

        val listPID = arrayListOf<Int>()
        val listPName = arrayListOf<String>()
        val listPTime = arrayListOf<String>()
        val listPIsNotified = arrayListOf<Int>()
        val listPCity = arrayListOf<String>()

        selList.forEach {
            listPID.add(it.prayerID)
            listPName.add(it.prayerName)
            listPTime.add(it.prayerTime)

            if(it.isNotified)
                listPIsNotified.add(1)
            else
                listPIsNotified.add(0)

            if(mCityName.isNullOrEmpty())
                listPCity.add("-")
            else
                listPCity.add(mCityName!!)
        }

        val b = Bundle()
        b.putIntegerArrayList("list_PID", listPID)
        b.putStringArrayList("list_PName", listPName)
        b.putStringArrayList("list_PTime", listPTime)
        b.putIntegerArrayList("list_PIsNotified", listPIsNotified)
        b.putStringArrayList("list_PCity", listPCity)

        return b
    }

}