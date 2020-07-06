package com.programmergabut.solatkuy.broadcaster

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.ui.activityprayer.DuaActivity
import com.programmergabut.solatkuy.util.enumclass.EnumConfig
import com.programmergabut.solatkuy.util.helper.NotificationHelper
import com.programmergabut.solatkuy.util.generator.DuaGenerator
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 27/03/20.
 */

class PrayerBroadcastReceiver: BroadcastReceiver() {

    private var pID: Int? = null
    private var pName: String? = null
    private var pTime: String? = null
    private var pCity: String? = null

    companion object{
        const val prayer_id = "prayer_id"
        const val prayer_name = "prayer_name"
        const val prayer_time = "prayer_time"
        const val prayer_city = "prayer_city"
        const val list_prayer_bundle = "list_prayer_bundle"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val mNotificationHelper = NotificationHelper(context!!)

        pID = intent?.getIntExtra(prayer_id, -1)
        pName = intent?.getStringExtra(prayer_name)
        pTime = intent?.getStringExtra(prayer_time)
        pCity = intent?.getStringExtra(prayer_city)
        val listPrayerBundle = intent?.extras?.getBundle(list_prayer_bundle)

        val listData = bundleDeserializer(listPrayerBundle)

        if(pID == -1 || pName.isNullOrEmpty() || pTime.isNullOrEmpty() || listPrayerBundle == null)
            throw Exception("PrayerBroadcastReceiver")

        removeAllNotification(context, listData)

        if(pCity == null) pCity = "-"

        val duaIntent = intentToDuaAfterAdhanGenerator(context)

        /*
        * Deprecated, 4 June 2020
        * because change the notification mechanism to fire all the data
        * then cancel all alarm manager when the notification come, then fire it all again
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

        val pendingIntent = PendingIntent.getActivity(context, EnumConfig.nIdDua, duaIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nb = mNotificationHelper.getPrayerReminderNC(/* pID!!,*/ pTime!!, pCity!!, pName!!, /* listPrayerBundle, */ pendingIntent)
        mNotificationHelper.getManager()?.notify(EnumConfig.nIdMain, nb.build())

        executeNextNotification(listData, listPrayerBundle, context, pCity!!)

    }

    private fun removeAllNotification(context: Context, listData: MutableList<NotifiedPrayer>){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PrayerBroadcastReceiver::class.java)

        for (id in 1 until listData.size){
            val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun executeNextNotification(listData: MutableList<NotifiedPrayer>, listPrayerBundle: Bundle?, context: Context, pCity: String) {

        val intent = Intent(context, PrayerBroadcastReceiver::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        /* remove sunrise */
        val newList = listData.filter { x -> x.prayerName !=  EnumConfig.sunrise} as MutableList<NotifiedPrayer>

        newList.sortBy { x -> x.prayerID }
        newList.forEachIndexed { _, it ->

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
        }

        /*
        * Deprecated, 4 June 2020
        * because change the notification mechanism to fire all the data
        * then cancel all alarm manager when the notification come, then fire it all again
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

        val listPID  =  listPrayerBundle?.getIntegerArrayList("list_PID")
        val listPName = listPrayerBundle?.getStringArrayList("list_PName")
        val listPTime = listPrayerBundle?.getStringArrayList("list_PTime")
        val listPIsNotified = listPrayerBundle?.getIntegerArrayList("list_PIsNotified")
        //val listPCity = listPrayerBundle?.getStringArrayList("list_PCity")

        val listCount = listPID?.count()

        for(i in 0 until listCount!!){

            val isNotified: Boolean = listPIsNotified!![i] == 1

            listData.add(
                NotifiedPrayer(
                    listPID[i],
                    listPName?.get(i)!!,
                    isNotified,
                    listPTime?.get(i)!!
                )
            )
        }

        return listData
    }

    private fun intentToDuaAfterAdhanGenerator(context: Context): Intent {
        val i = Intent(context, DuaActivity::class.java)

        val duaAfterAdhan = DuaGenerator.getListDua().find { x -> x.id == 1}

        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        i.putExtra(DuaActivity.duaTitle, duaAfterAdhan?.title)
        i.putExtra(DuaActivity.duaAr, duaAfterAdhan?.arab)
        i.putExtra(DuaActivity.duaLt, duaAfterAdhan?.latin)
        i.putExtra(DuaActivity.duaEn, duaAfterAdhan?.english)
        i.putExtra(DuaActivity.duaIn, duaAfterAdhan?.indonesia)
        i.putExtra(DuaActivity.duaRef, duaAfterAdhan?.reference)

        return i
    }

}