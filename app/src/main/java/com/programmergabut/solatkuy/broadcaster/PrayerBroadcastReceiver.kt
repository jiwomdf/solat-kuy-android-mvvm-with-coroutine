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
import com.programmergabut.solatkuy.util.helper.SelectPrayerHelper
import java.lang.Exception
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 27/03/20.
 */

class PrayerBroadcastReceiver: BroadcastReceiver() {

    private var pID: Int? = null
    private var pName: String? = null
    private var pTime: String? = null
    private var pCity: String? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val mNotificationHelper =
            NotificationHelper(context!!)

        pID = intent?.getIntExtra("prayer_id", -1)
        pName = intent?.getStringExtra("prayer_name")
        pTime = intent?.getStringExtra("prayer_time")
        pCity = intent?.getStringExtra("prayer_city")
        val listPrayerBundle = intent?.extras?.getBundle("list_prayer_bundle")

        if(pID == -1 || pName.isNullOrEmpty() || pTime.isNullOrEmpty() || listPrayerBundle == null)
            throw Exception("PrayerBroadcastReceiver")

        if(pCity == null)
            pCity = "-"

        val nextIntent = intentGenerator(context)

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
        }

        removeFirstNotification(context)
    }

    private fun removeFirstNotification(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PrayerBroadcastReceiver::class.java)

        for (id in 1..5){
            val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun intentGenerator(context: Context): Intent {
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

    private fun executeNextNotification(listPrayerBundle: Bundle?, context: Context, pCity: String) {

        val listData = bundleDeserializer(listPrayerBundle)

        val intent = Intent(context, PrayerBroadcastReceiver::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        /* remove sunrise */
        val newList = listData.filter { x -> x.prayerName !=  EnumConfig.sunrise} as MutableList<NotifiedPrayer>

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
        }

    }

    private fun bundleDeserializer(listPrayerBundle: Bundle?): MutableList<NotifiedPrayer> {

        val listData = mutableListOf<NotifiedPrayer>()

        val listPID  =  listPrayerBundle?.getIntegerArrayList("list_PID")
        val listPName = listPrayerBundle?.getStringArrayList("list_PName")
        val listPTime = listPrayerBundle?.getStringArrayList("list_PTime")
        val listPIsNotified = listPrayerBundle?.getIntegerArrayList("list_PIsNotified")
        val listPCity = listPrayerBundle?.getStringArrayList("list_PCity")

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

}