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
import com.programmergabut.solatkuy.ui.dua.DuaActivity
import com.programmergabut.solatkuy.util.EnumConfig
import com.programmergabut.solatkuy.util.LogConfig.Companion.ERROR
import com.programmergabut.solatkuy.ui.NotificationHelper
import com.programmergabut.solatkuy.data.hardcodedata.DuaData
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
        if(context == null || intent == null){
            val errMsg = "context == null || intent == null"
            Log.d(ERROR,errMsg)
            throw NullPointerException("PrayerBroadcastReceiver $errMsg")
        }

        val notificationHelper = NotificationHelper(context)
        val prayerID = intent.getIntExtra(PRAYER_ID, -1)
        val prayerName = intent.getStringExtra(PRAYER_NAME)
        val prayerTime = intent.getStringExtra(PRAYER_TIME)
        val prayerCity = intent.getStringExtra(PRAYER_CITY)
        val listPrayerBundle = intent.extras?.getBundle(LIST_PRAYER_BUNDLE)
        val city = prayerCity ?: "-"
        if(prayerID == -1 || prayerName.isNullOrEmpty() || prayerTime.isNullOrEmpty() || listPrayerBundle == null){
            val errMsg = "PrayerBroadcastReceiver, pID == -1 || pName.isNullOrEmpty() || pTime.isNullOrEmpty() || listPrayerBundle == null"
            Log.d(ERROR,errMsg)
            throw NullPointerException("PrayerBroadcastReceiver $errMsg")
        }

        val listData = bundleDeserializer(listPrayerBundle)
        if(listData.isNullOrEmpty()){
            val errMsg = "listData.isNullOrEmpty()"
            Log.d(ERROR,errMsg)
            throw Exception("PrayerBroadcastReceiver $errMsg")
        }
        removeAllNotification(context, listData)

        val duaIntent = intentToDuaAfterAdhanGenerator(context)
        val pendingIntent = PendingIntent.getActivity(context, EnumConfig.ID_DUA_PENDING_INTENT, duaIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationBuilder = notificationHelper.getPrayerReminderNC(prayerTime, city, prayerName, pendingIntent)
        notificationHelper.getManager()?.notify(EnumConfig.ID_PRAYER_NOTIFICATION, notificationBuilder.build())
        executeNextNotification(listData, listPrayerBundle, context, city)
    }

    private fun removeAllNotification(context: Context, listData: List<NotifiedPrayer>){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PrayerBroadcastReceiver::class.java)
        for (id in 1 until listData.size){
            val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun executeNextNotification(listPrayer: List<NotifiedPrayer>, listPrayerBundle: Bundle?, context: Context, pCity: String) {
        val intent = Intent(context, PrayerBroadcastReceiver::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val newPrayerList = listPrayer.filter { x -> x.prayerName !=  EnumConfig.SUNRISE} // remove img_sunrise

        newPrayerList.toMutableList().sortBy { prayer -> prayer.prayerID }
        newPrayerList.forEach{ prayer ->
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
                putExtra(PRAYER_ID, prayer.prayerID)
                putExtra(PRAYER_NAME, prayer.prayerName)
                putExtra(PRAYER_TIME, prayer.prayerTime)
                putExtra(PRAYER_CITY, pCity)
                putExtra(LIST_PRAYER_BUNDLE, listPrayerBundle)
            }

            val pendingIntent = PendingIntent.getBroadcast(context, prayer.prayerID, intent, 0)
            if(calendar.before(Calendar.getInstance()))
                calendar.add(Calendar.DATE, 1)

            if(prayer.isNotified){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                else
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
            else{
                alarmManager.cancel(pendingIntent)
            }
        }
    }

    private fun bundleDeserializer(listPrayerBundle: Bundle): List<NotifiedPrayer> {
        val listData = mutableListOf<NotifiedPrayer>()
        val listPrayerID  =  listPrayerBundle.getIntegerArrayList(LIST_PRAYER_ID)
        val listPrayerName = listPrayerBundle.getStringArrayList(LIST_PRAYER_NAME)
        val listPrayerTime = listPrayerBundle.getStringArrayList(LIST_PRAYER_TIME)
        val listPrayerIsNotified = listPrayerBundle.getIntegerArrayList(LIST_PRAYER_IS_NOTIFIED)

        if(listPrayerID.isNullOrEmpty() || listPrayerName.isNullOrEmpty() || listPrayerTime.isNullOrEmpty() || listPrayerIsNotified.isNullOrEmpty()){
            return listOf()
        }

        for(i in 0 until listPrayerID.count()){
            val isNotified: Boolean = listPrayerIsNotified[i] == 1
            listData.add(NotifiedPrayer(listPrayerID[i], listPrayerName[i]!!, isNotified, listPrayerTime[i]!!))
        }
        return listData
    }

    private fun intentToDuaAfterAdhanGenerator(context: Context): Intent {
        val intent = Intent(context, DuaActivity::class.java)
        val duaAfterAdhan = DuaData.getListDua().find { dua -> dua.id == 1}
        intent.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(DuaActivity.DUA_TITLE, duaAfterAdhan?.title)
            putExtra(DuaActivity.DUA_AR, duaAfterAdhan?.arab)
            putExtra(DuaActivity.DUA_LT, duaAfterAdhan?.latin)
            putExtra(DuaActivity.DUA_EN, duaAfterAdhan?.english)
            putExtra(DuaActivity.DUA_IN, duaAfterAdhan?.indonesia)
            putExtra(DuaActivity.DUA_REF, duaAfterAdhan?.reference)
        }
        return intent
    }

}