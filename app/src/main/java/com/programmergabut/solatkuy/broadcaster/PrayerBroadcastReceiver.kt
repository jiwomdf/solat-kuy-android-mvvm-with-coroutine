package com.programmergabut.solatkuy.broadcaster

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.programmergabut.solatkuy.data.local.localentity.NotifiedPrayer
import com.programmergabut.solatkuy.ui.dua.DuaActivity
import com.programmergabut.solatkuy.util.EnumConfig
import com.programmergabut.solatkuy.ui.NotificationHelper
import com.programmergabut.solatkuy.data.hardcodedata.DuaData
import com.programmergabut.solatkuy.model.DuaExtraData
import com.programmergabut.solatkuy.model.PrayerExtraData
import com.programmergabut.solatkuy.model.PrayerListExtraData
import java.util.*

/*
 * Created by Katili Jiwo Adi Wiyono on 27/03/20.
 */

class PrayerBroadcastReceiver: BroadcastReceiver() {

    companion object{
        const val prayerID = "prayerId"
        const val prayerName = "prayerName"
        const val prayerTime = "prayerTime"
        const val prayerCity = "prayerCity"

        const val PrayerListData = "prayer_list_data"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(context == null || intent == null){
            throw NullPointerException()
        }

        val notificationHelper = NotificationHelper(context)

        val prayerData = PrayerExtraData().also {
            it.prayerId = intent.getIntExtra(prayerID, 0)
            it.prayerName = intent.getStringExtra(prayerName)
            it.prayerTime = intent.getStringExtra(prayerTime)
            it.prayerCity = intent.getStringExtra(prayerCity)
        }

        if(prayerData.prayerName.isNullOrEmpty() || prayerData.prayerTime.isNullOrEmpty()){
            throw NullPointerException()
        }

        //val listData = bundleDeserializer(prayerData.listPrayerBundle)
        //if(listData.isNullOrEmpty()){
        //    throw NullPointerException()
        //}

        //removeAllNotification(context, listData)
        removeAllNotification(context)

        val duaIntent = createDuaIntent(context)
        val pendingIntent = PendingIntent.getActivity(context, EnumConfig.ID_DUA_PENDING_INTENT, duaIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationBuilder = notificationHelper.createPrayerReminderNotification(prayerData.prayerTime!!,
            prayerData.prayerCity ?: "-", prayerData.prayerName!!, pendingIntent)

        notificationHelper.getManager()?.notify(EnumConfig.ID_PRAYER_NOTIFICATION, notificationBuilder.build())
        //executeNextNotification(listData, prayerData.listPrayerBundle, context, city)
    }

    private fun removeAllNotification(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PrayerBroadcastReceiver::class.java)
        for (id in 1 until 5){
            val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun removeAllNotification(context: Context, listData: List<NotifiedPrayer>){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PrayerBroadcastReceiver::class.java)
        for (id in 1 until listData.size){
            val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
            alarmManager.cancel(pendingIntent)
        }
    }

    /* private fun executeNextNotification(listPrayer: List<NotifiedPrayer>, listPrayerBundle: Bundle?, context: Context, pCity: String) {
        val intent = Intent(context, PrayerBroadcastReceiver::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val newPrayerList = listPrayer.filter { x -> x.prayerName !=  EnumConfig.SUNRISE} // remove img_sunrise
        newPrayerList.toMutableList().sortBy { prayer -> prayer.prayerID }

        newPrayerList.forEach{ prayer ->

            intent.putExtra(PrayerData, PrayerExtraData(
               prayerId = prayer.prayerID,
               prayerName = prayer.prayerName,
               prayerTime = prayer.prayerTime,
               prayerCity = pCity,
               listPrayerBundle = listPrayerBundle
            ))

            val pendingIntent = PendingIntent.getBroadcast(context, prayer.prayerID, intent, 0)

            val calendar = createCalendar(prayer)
            if(calendar.before(Calendar.getInstance()))
                calendar.add(Calendar.DATE, 1)

            fireAlarmManager(prayer, pendingIntent, alarmManager, calendar)
        }
    } */

    /* private fun fireAlarmManager(
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
    } */

    /* private fun bundleDeserializer(listPrayerBundle: Bundle): List<NotifiedPrayer> {
        val extra = listPrayerBundle.getParcelable<PrayerListExtraData>(PrayerListData)
        if(extra?.listPrayerID.isNullOrEmpty() || extra?.listPrayerName.isNullOrEmpty() ||
            extra?.listPrayerTime.isNullOrEmpty() || extra?.listPrayerIsNotified.isNullOrEmpty()){
            return listOf()
        }

        val listData = mutableListOf<NotifiedPrayer>()
        for(i in 0 until extra!!.listPrayerID.count()){
            listData.add(NotifiedPrayer(
                extra.listPrayerID[i],
                extra.listPrayerName[i],
                extra.listPrayerIsNotified[i] == 1,
                extra.listPrayerTime[i]
            ))
        }
        return listData
    } */

    private fun createDuaIntent(context: Context): Intent {
        val intent = Intent(context, DuaActivity::class.java)
        val duaAfterAdhan = DuaData.getListDua().find { dua -> dua.id == 1}!!
        intent.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(DuaActivity.DuaData, DuaExtraData(
                duaTitle = duaAfterAdhan.title,
                duaAr = duaAfterAdhan.arab,
                duaLt = duaAfterAdhan.latin,
                duaEn = duaAfterAdhan.english,
                duaIn = duaAfterAdhan.indonesia,
                duaRef = duaAfterAdhan.reference
            ))
        }
        return intent
    }

}