package com.programmergabut.solatkuy.broadcaster

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.programmergabut.solatkuy.ui.dua.DuaActivity
import com.programmergabut.solatkuy.util.Constant
import com.programmergabut.solatkuy.ui.NotificationHelper
import com.programmergabut.solatkuy.data.hardcodedata.DuaData
import com.programmergabut.solatkuy.model.DuaExtraData
import com.programmergabut.solatkuy.model.PrayerExtraData

/*
 * Created by Katili Jiwo Adi Wiyono on 27/03/20.
 */

class PrayerBroadcastReceiver: BroadcastReceiver() {

    companion object{
        const val prayerID = "prayerId"
        const val prayerName = "prayerName"
        const val prayerTime = "prayerTime"
        const val prayerCity = "prayerCity"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(context == null || intent == null){
            FirebaseCrashlytics.getInstance().setCustomKey("onReceive PrayerBroadcastReceiver", "context == null || intent == null")
            throw NullPointerException()
        }

        val notificationHelper = NotificationHelper(context)
        val prayerData = setIntent(intent)

        if(prayerData.prayerName.isNullOrEmpty() || prayerData.prayerTime.isNullOrEmpty()){
            FirebaseCrashlytics.getInstance().setCustomKey("onReceive PrayerBroadcastReceiver", "prayerData.prayerName.isNullOrEmpty() || prayerData.prayerTime.isNullOrEmpty()")
            throw NullPointerException()
        }

        removeAllNotification(context)

        val duaIntent = createDuaIntent(context)
        val pendingIntent = PendingIntent.getActivity(context, Constant.ID_DUA_PENDING_INTENT, duaIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationBuilder = notificationHelper.createPrayerReminderNotification(prayerData.prayerTime!!,
            prayerData.prayerCity ?: "-", prayerData.prayerName!!, pendingIntent)

        notificationHelper.getManager()?.notify(Constant.ID_PRAYER_NOTIFICATION, notificationBuilder.build())
    }

    private fun setIntent(intent: Intent): PrayerExtraData {
        return PrayerExtraData().also {
            it.prayerId = intent.getIntExtra(prayerID, 0)
            it.prayerName = intent.getStringExtra(prayerName)
            it.prayerTime = intent.getStringExtra(prayerTime)
            it.prayerCity = intent.getStringExtra(prayerCity)
        }
    }

    private fun removeAllNotification(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PrayerBroadcastReceiver::class.java)
        for (id in 1 until 5){
            val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
            alarmManager.cancel(pendingIntent)
        }
    }

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