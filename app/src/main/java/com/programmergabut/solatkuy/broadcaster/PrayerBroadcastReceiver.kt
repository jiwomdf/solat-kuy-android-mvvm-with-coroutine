package com.programmergabut.solatkuy.broadcaster

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.ui.DuaActivity
import com.programmergabut.solatkuy.util.EnumConfig
import com.programmergabut.solatkuy.util.helper.NotificationHelper
import com.programmergabut.solatkuy.util.hardcodedata.DuaData
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 27/03/20.
 */

class PrayerBroadcastReceiver: BroadcastReceiver() {

    companion object{
        const val PRAYER_ID = "prayer_id"
        const val PRAYER_NAME = "prayer_name"
        const val PRAYER_TIME = "prayer_time"
        const val PRAYER_CITY = "prayer_city"
        const val LIST_PRAYER_BUNDLE = "list_prayer_bundle"

        const val LIST_PRAYER_ID = "list_PID"
        const val LIST_PRAYER_NAME = "list_PName"
        const val LIST_PRAYER_TIME = "list_PTime"
        const val LIST_PRAYER_IS_NOTIFIED = "list_PIsNotified"
        const val LIST_PRAYER_CITY = "listPrayerCity"
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("<ERROR>","PrayerBroadcastReceiver, pID == -1 || pName.isNullOrEmpty() || pTime.isNullOrEmpty() || listPrayerBundle == null")

        val mNotificationHelper = NotificationHelper(context!!)

        val pID = intent?.getIntExtra(PRAYER_ID, -1)
        val pName = intent?.getStringExtra(PRAYER_NAME)
        val pTime = intent?.getStringExtra(PRAYER_TIME)
        var pCity = intent?.getStringExtra(PRAYER_CITY)
        val listPrayerBundle = intent?.extras?.getBundle(LIST_PRAYER_BUNDLE)

        val listData = bundleDeserializer(listPrayerBundle)

        if(pID == -1 || pName.isNullOrEmpty() || pTime.isNullOrEmpty() || listPrayerBundle == null){
            Log.d("<ERROR>","PrayerBroadcastReceiver, pID == -1 || pName.isNullOrEmpty() || pTime.isNullOrEmpty() || listPrayerBundle == null")
            throw Exception("PrayerBroadcastReceiver")
        }

        removeAllNotification(context, listData)

        if(pCity == null)
            pCity = "-"

        val duaIntent = intentToDuaAfterAdhanGenerator(context)

        /*
        * Deprecated, 4 June 2020
        * because the changes of the notification mechanism
        * first fire all the data then cancel all alarm manager when the notification come, then fire it all again
        * also remove the more time feature

          when (pID!!) {
            EnumConfig.nId1 -> {
                val pendingIntent = PendingIntent.getActivity(context, EnumConfig.nId1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val nb = mNotificationHelper.getPrayerReminderNC(EnumConfig.nId1, pTime!!, pCity!!, pName!!, listPrayerBundle, pendingIntent)
                mNotificationHelper.getManager()?.notify(EnumConfig.nId1, nb.build())
            }
            EnumConfig.nId2 -> {
                val pendingIntent = PendingIntent.getActivity(context, EnumConfig.nId2, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val nb = mNotificationHelper.getPrayerReminderNC(EnumConfig.nId2, pTime!!, pCity!!, pName!!, listPrayerBundle, pendingIntent)
                mNotificationHelper.getManager()?.notify(EnumConfig.nId2, nb.build())
            }
            else -> {
                val pendingIntent = PendingIntent.getActivity(context, EnumConfig.nIdMain, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val nb = mNotificationHelper.getPrayerReminderNC(pID!!, pTime!!, pCity!!, pName!!, listPrayerBundle, pendingIntent)
                mNotificationHelper.getManager()?.notify(EnumConfig.nIdMain, nb.build())

                executeNextNotification(listPrayerBundle,context, pCity!!)
            }
        } */

        val pendingIntent = PendingIntent.getActivity(context, EnumConfig.ID_DUA_PENDING_INTENT, duaIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nb = mNotificationHelper.getPrayerReminderNC(/* pID!!,*/ pTime, pCity, pName, /* listPrayerBundle, */ pendingIntent)
        mNotificationHelper.getManager()?.notify(EnumConfig.ID_PRAYER_NOTIFICATION, nb.build())

        executeNextNotification(listData, listPrayerBundle, context, pCity)
    }

    private fun removeAllNotification(context: Context, listData: MutableList<NotifiedPrayer>){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PrayerBroadcastReceiver::class.java)

        for (id in 1 until listData.size){
            val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun executeNextNotification(listPrayer: MutableList<NotifiedPrayer>, listPrayerBundle: Bundle?, context: Context, pCity: String) {

        val intent = Intent(context, PrayerBroadcastReceiver::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        /* remove img_sunrise */
        val newPrayerList = listPrayer.filter { x -> x.prayerName !=  EnumConfig.SUNRISE} as MutableList<NotifiedPrayer>

        newPrayerList.sortBy { prayer -> prayer.prayerID }
        newPrayerList.forEach{ prayer ->

            val arrPrayer = prayer.prayerTime.split(":")
            val hour = arrPrayer[0].trim()
            val minute = arrPrayer[1].split(" ")[0].trim()

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour.toInt())
            calendar.set(Calendar.MINUTE, minute.toInt())
            calendar.set(Calendar.SECOND, 0)

            intent.putExtra(PRAYER_ID, prayer.prayerID)
            intent.putExtra(PRAYER_NAME, prayer.prayerName)
            intent.putExtra(PRAYER_TIME, prayer.prayerTime)
            intent.putExtra(PRAYER_CITY, pCity)
            intent.putExtra(LIST_PRAYER_BUNDLE, listPrayerBundle)

            val pendingIntent = PendingIntent.getBroadcast(context, prayer.prayerID, intent, 0)

            if(calendar.before(Calendar.getInstance()))
                calendar.add(Calendar.DATE, 1)

            if(prayer.isNotified)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                else
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            else
                alarmManager.cancel(pendingIntent)
        }

        /*
        * Deprecated, 4 June 2020
        * because the changes of the notification mechanism
        * first fire all the data then cancel all alarm manager when the notification come, then fire it all again
        * also remove the more time feature

        val selID = SelectPrayerHelper.selNextPrayerByLastID(newList, pID!!)
        //val selID = NotifiedPrayer(2,"mantap 2",true,"18:46") //#testing purpose

        selID?.let{

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
            intent.putExtra("prayer_city", pCity)
            intent.putExtra("list_prayer_bundle", listPrayerBundle)

            val pendingIntent = PendingIntent.getBroadcast(context, it.prayerID, intent, 0)

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

    private fun bundleDeserializer(listPrayerBundle: Bundle?): MutableList<NotifiedPrayer> {

        val listData = mutableListOf<NotifiedPrayer>()

        val listPrayerID  =  listPrayerBundle?.getIntegerArrayList(LIST_PRAYER_ID)
        val listPrayerName = listPrayerBundle?.getStringArrayList(LIST_PRAYER_NAME)
        val listPrayerTime = listPrayerBundle?.getStringArrayList(LIST_PRAYER_TIME)
        val listPrayerIsNotified = listPrayerBundle?.getIntegerArrayList(LIST_PRAYER_IS_NOTIFIED)
        /* val listPCity = listPrayerBundle?.getStringArrayList(LIST_PRAYER_CITY) */

        val listCount = listPrayerID?.count()

        for(i in 0 until listCount!!){

            val isNotified: Boolean = listPrayerIsNotified!![i] == 1

            listData.add(
                NotifiedPrayer(
                    listPrayerID[i],
                    listPrayerName?.get(i)!!,
                    isNotified,
                    listPrayerTime?.get(i)!!
                )
            )
        }

        return listData
    }

    private fun intentToDuaAfterAdhanGenerator(context: Context): Intent {
        val intent = Intent(context, DuaActivity::class.java)
        val duaAfterAdhan = DuaData.getListDua().find { x -> x.id == 1}

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(DuaActivity.DUA_TITLE, duaAfterAdhan?.title)
        intent.putExtra(DuaActivity.DUA_AR, duaAfterAdhan?.arab)
        intent.putExtra(DuaActivity.DUA_LT, duaAfterAdhan?.latin)
        intent.putExtra(DuaActivity.DUA_EN, duaAfterAdhan?.english)
        intent.putExtra(DuaActivity.DUA_IN, duaAfterAdhan?.indonesia)
        intent.putExtra(DuaActivity.DUA_REF, duaAfterAdhan?.reference)

        return intent
    }

}